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
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from 'vue'
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import TextAlign from '@tiptap/extension-text-align'
import CharacterCount from '@tiptap/extension-character-count'
import Placeholder from '@tiptap/extension-placeholder'
import Underline from '@tiptap/extension-underline'

// Props
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

// Emits
const emit = defineEmits(['update:modelValue'])

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
    // Emit HTML content for v-model binding
    emit('update:modelValue', editor.getHTML())
  },
})

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