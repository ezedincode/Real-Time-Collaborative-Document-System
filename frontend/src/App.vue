<template>
  <div class="editor-container">
    <!-- Toolbar -->
    <div v-if="editor" class="toolbar">
      <!-- Text Formatting -->
      <button
        @click="editor.chain().focus().toggleBold().run()"
        :class="{ 'is-active': editor.isActive('bold') }"
        class="toolbar-btn"
        title="Bold"
      >
        <strong>B</strong>
      </button>
      
      <button
        @click="editor.chain().focus().toggleItalic().run()"
        :class="{ 'is-active': editor.isActive('italic') }"
        class="toolbar-btn"
        title="Italic"
      >
        <em>I</em>
      </button>
      
      <button
        @click="editor.chain().focus().toggleStrike().run()"
        :class="{ 'is-active': editor.isActive('strike') }"
        class="toolbar-btn"
        title="Strikethrough"
      >
        <s>S</s>
      </button>
      
      <span class="separator"></span>
      
      <!-- Headings -->
      <button
        @click="editor.chain().focus().toggleHeading({ level: 1 }).run()"
        :class="{ 'is-active': editor.isActive('heading', { level: 1 }) }"
        class="toolbar-btn"
        title="Heading 1"
      >
        H1
      </button>
      
      <button
        @click="editor.chain().focus().toggleHeading({ level: 2 }).run()"
        :class="{ 'is-active': editor.isActive('heading', { level: 2 }) }"
        class="toolbar-btn"
        title="Heading 2"
      >
        H2
      </button>
      
      <button
        @click="editor.chain().focus().toggleHeading({ level: 3 }).run()"
        :class="{ 'is-active': editor.isActive('heading', { level: 3 }) }"
        class="toolbar-btn"
        title="Heading 3"
      >
        H3
      </button>
      
      <span class="separator"></span>
      
      <!-- Lists -->
      <button
        @click="editor.chain().focus().toggleBulletList().run()"
        :class="{ 'is-active': editor.isActive('bulletList') }"
        class="toolbar-btn"
        title="Bullet List"
      >
        • List
      </button>
      
      <button
        @click="editor.chain().focus().toggleOrderedList().run()"
        :class="{ 'is-active': editor.isActive('orderedList') }"
        class="toolbar-btn"
        title="Numbered List"
      >
        1. List
      </button>
      
      <span class="separator"></span>
      
      <!-- Alignment -->
      <button
        @click="editor.chain().focus().setTextAlign('left').run()"
        :class="{ 'is-active': editor.isActive({ textAlign: 'left' }) }"
        class="toolbar-btn"
        title="Align Left"
      >
        ←
      </button>
      
      <button
        @click="editor.chain().focus().setTextAlign('center').run()"
        :class="{ 'is-active': editor.isActive({ textAlign: 'center' }) }"
        class="toolbar-btn"
        title="Align Center"
      >
        ↔
      </button>
      
      <button
        @click="editor.chain().focus().setTextAlign('right').run()"
        :class="{ 'is-active': editor.isActive({ textAlign: 'right' }) }"
        class="toolbar-btn"
        title="Align Right"
      >
        →
      </button>
      
      <span class="separator"></span>
      
      <!-- Undo/Redo -->
      <button
        @click="editor.chain().focus().undo().run()"
        :disabled="!editor.can().undo()"
        class="toolbar-btn"
        title="Undo"
      >
        ↩
      </button>
      
      <button
        @click="editor.chain().focus().redo().run()"
        :disabled="!editor.can().redo()"
        class="toolbar-btn"
        title="Redo"
      >
        ↪
      </button>
    </div>
    
    <!-- Editor Content -->
    <editor-content :editor="editor" class="editor-content" />
    
    <!-- Document Status Bar -->
    <div v-if="editor" class="status-bar">
      <div class="status-item">
        <span class="status-label">Words:</span>
        <span class="status-value">{{ wordCount }}</span>
      </div>
      <div class="status-item">
        <span class="status-label">Characters:</span>
        <span class="status-value">{{ characterCount }}</span>
      </div>
      <div class="status-item">
        <span class="status-label">Lines:</span>
        <span class="status-value">{{ lineCount }}</span>
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

// Clean up
onBeforeUnmount(() => {
  editor.value?.destroy()
})
</script>

<style scoped>
.editor-container {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
  background: white;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding: 8px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.toolbar-btn {
  padding: 6px 12px;
  border: 1px solid transparent;
  background: transparent;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.toolbar-btn:hover {
  background: #e2e8f0;
}

.toolbar-btn.is-active {
  background: #3b82f6;
  color: white;
  border-color: #2563eb;
}

.toolbar-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.separator {
  width: 1px;
  background: #cbd5e1;
  margin: 0 4px;
}

.editor-content {
  min-height: 300px;
  padding: 16px;
}

.status-bar {
  display: flex;
  gap: 24px;
  padding: 8px 16px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  font-size: 13px;
}

.status-item {
  display: flex;
  gap: 8px;
}

.status-label {
  color: #64748b;
  font-weight: 500;
}

.status-value {
  color: #0f172a;
  font-weight: 600;
}

/* Placeholder styling */
:deep(.ProseMirror p.is-editor-empty:first-child::before) {
  color: #adb5bd;
  content: attr(data-placeholder);
  float: left;
  height: 0;
  pointer-events: none;
}
</style>