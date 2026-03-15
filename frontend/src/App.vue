<template>
  <div class="min-h-screen w-full border border-slate-200 rounded-none overflow-hidden bg-white">
    <!-- Toolbar -->
    <div
      v-if="editor"
      class="flex flex-wrap gap-1 p-2 bg-slate-50 border-b border-slate-200"
    >
      <!-- Text Formatting -->
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
      
      <span class="w-px bg-slate-300 mx-1 self-stretch"></span>
      
      <!-- Headings -->
      <button
        @click="editor.chain().focus().toggleHeading({ level: 1 }).run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive('heading', { level: 1 })
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Heading 1"
      >
        H1
      </button>
      
      <button
        @click="editor.chain().focus().toggleHeading({ level: 2 }).run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive('heading', { level: 2 })
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Heading 2"
      >
        H2
      </button>
      
      <button
        @click="editor.chain().focus().toggleHeading({ level: 3 }).run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive('heading', { level: 3 })
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Heading 3"
      >
        H3
      </button>
      
      <span class="w-px bg-slate-300 mx-1 self-stretch"></span>
      
      <!-- Lists -->
      <button
        @click="editor.chain().focus().toggleBulletList().run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive('bulletList')
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Bullet List"
      >
        • List
      </button>
      
      <button
        @click="editor.chain().focus().toggleOrderedList().run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive('orderedList')
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Numbered List"
      >
        1. List
      </button>
      
      <span class="w-px bg-slate-300 mx-1 self-stretch"></span>
      
      <!-- Alignment -->
      <button
        @click="editor.chain().focus().setTextAlign('left').run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive({ textAlign: 'left' })
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Align Left"
      >
        ←
      </button>
      
      <button
        @click="editor.chain().focus().setTextAlign('center').run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive({ textAlign: 'center' })
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Align Center"
      >
        ↔
      </button>
      
      <button
        @click="editor.chain().focus().setTextAlign('right').run()"
        :class="[
          toolbarButtonBaseClasses,
          editor.isActive({ textAlign: 'right' })
            ? toolbarButtonActiveClasses
            : toolbarButtonInactiveClasses,
        ]"
        title="Align Right"
      >
        →
      </button>
      
      <span class="w-px bg-slate-300 mx-1 self-stretch"></span>
      
      <!-- Undo/Redo -->
      <button
        @click="editor.chain().focus().undo().run()"
        :disabled="!editor.can().undo()"
        :class="[toolbarButtonBaseClasses, toolbarButtonInactiveClasses]"
        title="Undo"
      >
        ↩
      </button>
      
      <button
        @click="editor.chain().focus().redo().run()"
        :disabled="!editor.can().redo()"
        :class="[toolbarButtonBaseClasses, toolbarButtonInactiveClasses]"
        title="Redo"
      >
        ↪
      </button>
    </div>
    
    <!-- Editor Content -->
    <editor-content
      :editor="editor"
      class="min-h-[300px] p-4 outline-none"
    />
    
    <!-- Document Status Bar -->
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

    <!-- JSON Export / Import -->
    <div
      v-if="editor"
      class="px-4 py-3 bg-slate-50 border-t border-slate-200 text-xs space-y-2"
    >
      <div class="flex flex-wrap items-center gap-2">
        <span class="text-slate-500 font-medium">Document:</span>
        <button
          @click="saveToLocalStorage"
          :class="[toolbarButtonBaseClasses, toolbarButtonInactiveClasses]"
          type="button"
        >
          Save (Local Storage)
        </button>
        <button
          @click="loadFromLocalStorage"
          :class="[toolbarButtonBaseClasses, toolbarButtonInactiveClasses]"
          type="button"
        >
          Load (Local Storage)
        </button>
        <span class="text-slate-400 mx-1">|</span>
        <span class="text-slate-500 font-medium">JSON:</span>
        <button
          @click="exportJson"
          :class="[toolbarButtonBaseClasses, toolbarButtonInactiveClasses]"
          type="button"
        >
          Export
        </button>
        <button
          @click="importJson"
          :class="[toolbarButtonBaseClasses, toolbarButtonInactiveClasses]"
          type="button"
        >
          Load from JSON
        </button>
      </div>
      <textarea
        v-model="jsonState"
        class="w-full h-40 text-xs font-mono border border-slate-300 rounded p-2 bg-white focus:outline-none focus:ring-1 focus:ring-blue-500"
        spellcheck="false"
        placeholder="Exported document JSON will appear here. You can also paste JSON and click Load to restore a document."
      ></textarea>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount, onMounted, watch } from 'vue'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import TextAlign from '@tiptap/extension-text-align'
import CharacterCount from '@tiptap/extension-character-count'
import Placeholder from '@tiptap/extension-placeholder'
import Underline from '@tiptap/extension-underline'
import { useStateStore } from './stores/state'
import { connectWebSocket, sendDocumentEvent, disconnectWebSocket } from './service/webSocketService'

// Props
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

// Emits
const emit = defineEmits(['update:modelValue'])

// Global state
const stateStore = useStateStore()

// Track whether the current editor change came from the server
let isRemoteUpdate = false

// Initialize editor
const editor = useEditor({
  content: props.modelValue,
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
      limit: 10000, // Optional: set character limit
    }),
  ],
  onUpdate: ({ editor }) => {
    // If this update was triggered by a remote patch, don't echo it back
    if (isRemoteUpdate) {
      isRemoteUpdate = false
      return
    }

    // Emit plain-text content for v-model binding
    const text = editor.getText()
    emit('update:modelValue', text)

    // Update global state and notify backend about local changes (plain text only)
    const event = {
      id: stateStore.id,
      content: text,
    }
    stateStore.documetEvent = event
    sendDocumentEvent(event)
  },
})

// When backend pushes a new document event, update store and editor
onMounted(() => {
  connectWebSocket((event) => {
    stateStore.id = event.id
    stateStore.documetEvent = event

    if (!editor.value || !event.content) return

    // Avoid unnecessary updates and prevent feedback loops
    if (editor.value.getText() !== event.content) {
      isRemoteUpdate = true
      editor.value.commands.setContent(event.content, false)
    }
  })
})

// Keep editor in sync if document event changes from elsewhere
watch(
  () => stateStore.documetEvent?.content,
  (newContent) => {
    if (!editor.value || !newContent) return
    if (editor.value.getText() !== newContent) {
      isRemoteUpdate = true
      editor.value.commands.setContent(newContent, false)
    }
  }
)

// JSON state for export / import
const jsonState = ref('')

const LOCAL_STORAGE_KEY = 'rtcds-document-json'

const exportJson = () => {
  if (!editor.value) return
  const json = editor.value.getJSON()
  jsonState.value = JSON.stringify(json, null, 2)
}

const importJson = () => {
  if (!editor.value) return
  if (!jsonState.value.trim()) return
  try {
    const parsed = JSON.parse(jsonState.value)
    editor.value.commands.setContent(parsed)
  } catch (e) {
    // Basic error feedback without breaking the app
    window.alert('Invalid JSON: unable to load document state.')
  }
}

const saveToLocalStorage = () => {
  if (!editor.value) return
  try {
    const json = editor.value.getJSON()
    localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(json))
    window.alert('Document saved to local storage.')
  } catch (e) {
    window.alert('Failed to save document to local storage.')
  }
}

const loadFromLocalStorage = () => {
  if (!editor.value) return
  const saved = localStorage.getItem(LOCAL_STORAGE_KEY)
  if (!saved) {
    window.alert('No saved document found in local storage.')
    return
  }
  try {
    const parsed = JSON.parse(saved)
    editor.value.commands.setContent(parsed)
  } catch (e) {
    window.alert('Saved document in local storage is invalid.')
  }
}

// Document statistics
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

// Clean up
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

/* Ensure lists render clearly inside the editor */
:deep(.ProseMirror ul) {
  list-style-type: disc;
  padding-left: 1.5rem;
  margin: 0.5rem 0;
}

:deep(.ProseMirror ol) {
  list-style-type: decimal;
  padding-left: 1.5rem;
  margin: 0.5rem 0;
}

:deep(.ProseMirror li) {
  margin: 0.125rem 0;
}
</style>