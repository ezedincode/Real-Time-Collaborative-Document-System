<template>
  <div class="min-h-screen w-full border border-slate-200 rounded-none overflow-hidden bg-white">
    <div class="flex items-center justify-between px-4 py-3 border-b border-slate-200 bg-slate-50">
      <div class="text-sm text-slate-700 flex items-center gap-2">
        <span>
          Document ID:
          <span class="font-semibold text-slate-900">{{ stateStore.id || '-' }}</span>
        </span>
        <span
          class="px-2 py-0.5 rounded text-xs font-medium"
          :class="isPublicDocument ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'"
        >
          {{ isPublicDocument ? 'Public' : 'Private' }}
        </span>
      </div>
      <div class="flex items-center gap-2">
        <button
          type="button"
          class="px-3 py-1.5 text-sm border border-slate-300 rounded bg-white hover:bg-slate-100"
          @click="createNewDocument"
        >
          New Document
        </button>
        <button
          type="button"
          class="px-3 py-1.5 text-sm border border-slate-900 rounded bg-slate-900 text-white hover:bg-slate-800"
          @click="logout"
        >
          Logout
        </button>
      </div>
    </div>

    <div
      v-if="editor"
      class="flex flex-wrap gap-1 p-2 bg-slate-50 border-b border-slate-200"
    >
      <button
        @click="editor.chain().focus().toggleBold().run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive('bold')
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Bold"
      >
        <strong>B</strong>
      </button>

      <button
        @click="editor.chain().focus().toggleItalic().run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive('italic')
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Italic"
      >
        <em>I</em>
      </button>

      <button
        @click="editor.chain().focus().toggleStrike().run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive('strike')
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Strikethrough"
      >
        <s>S</s>
      </button>
    </div>

    <editor-content
      :editor="editor"
      class="min-h-87.5 p-4 outline-none"
    />

    <div
      v-if="editor"
      class="flex gap-6 px-4 py-2 bg-slate-50 border-t border-slate-200 text-xs"
    >
      <div class="flex gap-2 items-center">
        <span class="text-slate-500 font-medium">Words:</span>
        <span class="text-slate-900 font-semibold">{{ wordCount }}</span>
      </div>
      <div class="flex gap-2 items-center">
        <span class="text-slate-500 font-medium">Characters:</span>
        <span class="text-slate-900 font-semibold">{{ characterCount }}</span>
      </div>
      <div class="flex gap-2 items-center">
        <span class="text-slate-500 font-medium">Lines:</span>
        <span class="text-slate-900 font-semibold">{{ lineCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import TextAlign from '@tiptap/extension-text-align'
import CharacterCount from '@tiptap/extension-character-count'
import Placeholder from '@tiptap/extension-placeholder'
import Underline from '@tiptap/extension-underline'
import { useRoute, useRouter } from 'vue-router'
import { useStateStore } from '../stores/state'
import { useAuthStore } from '../stores/auth'
import { connectWebSocket, disconnectWebSocket, sendDocumentEvent } from '../service/webSocketService'

const route = useRoute()
const router = useRouter()
const stateStore = useStateStore()
const authStore = useAuthStore()
const isPublicDocument = ref(false)

const clientId = (typeof crypto !== 'undefined' && crypto.randomUUID)
  ? crypto.randomUUID()
  : Math.random().toString(36).slice(2)

const escapeHtml = (str) =>
  str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

const textToHtml = (text) => {
  const lines = (text ?? '').split('\n')
  return lines
    .map((line) => `<p>${escapeHtml(line)}</p>`)
    .join('')
}

let isRemoteUpdate = false

const editor = useEditor({
  content: '',
  extensions: [
    StarterKit,
    Underline,
    TextAlign.configure({
      types: ['heading', 'paragraph'],
    }),
    Placeholder.configure({
      placeholder: 'Start writing your document...',
      emptyEditorClass: 'is-editor-empty',
    }),
    CharacterCount.configure({
      limit: 10000,
    }),
  ],
  onUpdate: ({ editor }) => {
    if (isRemoteUpdate) {
      isRemoteUpdate = false
      return
    }

    const text = editor.getText()
    const event = {
      id: stateStore.id,
      content: text,
      clientId,
    }

    stateStore.documentEvent = event
    sendDocumentEvent(event)
  },
})

async function initializeDocument() {
  const rawId = String(route.params.id || '').trim()
  if (!rawId) {
    throw new Error('Missing document id in route')
  }

  if (rawId === 'new') {
    const makePublic = window.confirm(
      'Create this document as PUBLIC?\n\nOK = Public (other users can collaborate)\nCancel = Private (owner only)'
    )

    const created = await authStore.createDocument('', makePublic)
    await router.replace(`/document/${created.id}`)
    return initializeDocument()
  }

  const documentId = Number(rawId)
  if (Number.isNaN(documentId) || documentId <= 0) {
    throw new Error('Invalid document id')
  }

  const loaded = await authStore.getDocument(documentId)
  isPublicDocument.value = !!loaded.publicAccess
  stateStore.id = loaded.id
  stateStore.documentEvent = {
    id: loaded.id,
    content: loaded.content || '',
    clientId: '',
  }

  if (editor.value) {
    isRemoteUpdate = true
    editor.value.commands.setContent(textToHtml(loaded.content || ''), false)
  }
}

function connectForCurrentDocument() {
  connectWebSocket({
    token: authStore.accessToken,
    documentId: stateStore.id,
    onDocumentEvent: (event) => {
      if (event.clientId && event.clientId === clientId) {
        return
      }

      if (event.id !== stateStore.id) {
        return
      }

      stateStore.documentEvent = event

      if (!editor.value || event.content == null) {
        return
      }

      if (editor.value.getText() !== event.content) {
        isRemoteUpdate = true
        editor.value.commands.setContent(textToHtml(event.content), false)
      }
    },
    onError: () => {
      authStore.clearAuth()
      router.replace('/login')
    },
  })
}

async function createNewDocument() {
  await router.push('/document/new')
}

function logout() {
  disconnectWebSocket()
  authStore.clearAuth()
  router.replace('/login')
}

function handleDocumentLoadError(err) {
  if (err?.status === 403) {
    router.replace({
      path: '/unauthorized',
      query: { from: route.fullPath },
    })
    return
  }

  authStore.clearAuth()
  router.replace('/login')
}

onMounted(async () => {
  try {
    await initializeDocument()
    connectForCurrentDocument()
  } catch (err) {
    handleDocumentLoadError(err)
  }
})

watch(
  () => route.params.id,
  async () => {
    disconnectWebSocket()
    try {
      await initializeDocument()
      connectForCurrentDocument()
    } catch (err) {
      handleDocumentLoadError(err)
    }
  }
)

const wordCount = computed(() => {
  return editor.value?.storage.characterCount.words() || 0
})

const characterCount = computed(() => {
  return editor.value?.storage.characterCount.characters() || 0
})

const lineCount = computed(() => {
  if (!editor.value) return 0
  const text = editor.value.getText()
  return text.split('\n').length
})

const toolbarButtonBaseClasses =
  'px-2.5 py-1.5 rounded border border-transparent text-sm cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed'
const toolbarButtonInactiveClasses =
  'bg-transparent text-slate-800 hover:bg-slate-200'
const toolbarButtonActiveClasses =
  'bg-blue-500 text-white border-blue-600 hover:bg-blue-600'

onBeforeUnmount(() => {
  editor.value?.destroy()
  disconnectWebSocket()
})
</script>

<style scoped>
:deep(.ProseMirror p.is-editor-empty:first-child::before) {
  color: #94a3b8;
  content: attr(data-placeholder);
  float: left;
  height: 0;
  pointer-events: none;
}
</style>
