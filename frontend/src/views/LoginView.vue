<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-100 px-4">
    <div class="w-full max-w-md bg-white border border-slate-200 rounded-xl p-6 shadow-sm">
      <h1 class="text-2xl font-bold text-slate-900 mb-1">Realtime Docs</h1>
      <p class="text-sm text-slate-600 mb-6">Sign in to edit your documents.</p>

      <div class="flex gap-2 mb-4">
        <button
          type="button"
          class="px-3 py-2 rounded border text-sm"
          :class="mode === 'login' ? 'bg-slate-900 text-white border-slate-900' : 'bg-white text-slate-700 border-slate-300'"
          @click="mode = 'login'"
        >
          Login
        </button>
        <button
          type="button"
          class="px-3 py-2 rounded border text-sm"
          :class="mode === 'register' ? 'bg-slate-900 text-white border-slate-900' : 'bg-white text-slate-700 border-slate-300'"
          @click="mode = 'register'"
        >
          Register
        </button>
      </div>

      <form @submit.prevent="submit" class="space-y-4">
        <div>
          <label class="block text-sm text-slate-700 mb-1">Username</label>
          <input
            v-model.trim="username"
            type="text"
            class="w-full border border-slate-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900"
            required
          />
        </div>

        <div>
          <label class="block text-sm text-slate-700 mb-1">Password</label>
          <input
            v-model="password"
            type="password"
            class="w-full border border-slate-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900"
            required
          />
        </div>

        <p v-if="error" class="text-sm text-red-600">{{ error }}</p>

        <button
          type="submit"
          class="w-full px-4 py-2 bg-slate-900 text-white rounded text-sm hover:bg-slate-800 disabled:opacity-60"
          :disabled="loading"
        >
          {{ loading ? 'Please wait...' : mode === 'login' ? 'Login' : 'Create account' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const mode = ref('login')
const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function submit() {
  loading.value = true
  error.value = ''
  try {
    if (mode.value === 'login') {
      await authStore.login(username.value, password.value)
    } else {
      await authStore.register(username.value, password.value)
    }

    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/document/new'
    router.replace(redirect)
  } catch (err) {
    error.value = err?.message || 'Authentication failed'
  } finally {
    loading.value = false
  }
}
</script>
