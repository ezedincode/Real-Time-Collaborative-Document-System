<template>
  <div class="min-h-screen relative overflow-hidden bg-gradient-to-br from-amber-50 via-orange-50 to-teal-50 text-slate-900">
    <div class="absolute inset-0 pointer-events-none">
      <div class="absolute -top-28 right-0 h-72 w-72 rounded-full bg-amber-200/70 blur-3xl motion-safe:animate-pulse"></div>
      <div class="absolute top-20 left-1/4 h-40 w-40 rounded-full bg-teal-200/60 blur-3xl motion-safe:animate-pulse"></div>
      <div class="absolute bottom-[-6rem] left-[-6rem] h-96 w-96 rounded-full bg-rose-200/60 blur-3xl motion-safe:animate-pulse"></div>
      <div class="absolute top-24 right-1/3 h-24 w-24 rotate-12 border border-slate-900/15 bg-white/60"></div>
      <div class="absolute bottom-24 left-1/3 h-16 w-40 rounded-full border border-slate-900/10 bg-white/50 backdrop-blur"></div>
    </div>

    <div class="relative mx-auto flex min-h-screen max-w-6xl items-center px-6 py-12 font-sans">
      <div class="grid w-full gap-8 lg:grid-cols-[minmax(0,1.15fr)_minmax(0,0.85fr)]">
        <section class="rounded-3xl border border-slate-900/10 bg-white/70 p-6 shadow-sm backdrop-blur">
          <div class="inline-flex items-center gap-2 rounded-full border border-slate-900/10 bg-white/80 px-4 py-1.5 text-xs uppercase tracking-[0.25em] text-slate-600">
            Access denied
          </div>
          <h1 class="mt-5 text-3xl font-semibold tracking-tight text-slate-900 sm:text-5xl">
            You cannot open this document.
          </h1>
          <p class="mt-3 text-base text-slate-700 sm:text-lg">
            This document is private and only the owner can view it. You are signed in as
            <span class="font-semibold text-slate-900">{{ usernameLabel }}</span>.
          </p>

          <div class="mt-6 rounded-2xl border border-amber-200/80 bg-amber-50/80 p-4">
            <h2 class="text-sm font-semibold uppercase tracking-[0.2em] text-amber-800">Why this page is unavailable</h2>
            <ul class="mt-3 space-y-2 text-sm text-amber-900/90">
              <li class="flex gap-2">
                <span class="mt-1 h-2 w-2 rounded-full bg-amber-500"></span>
                The document is marked as owner-only.
              </li>
              <li class="flex gap-2">
                <span class="mt-1 h-2 w-2 rounded-full bg-amber-500"></span>
                Your account does not match the owner of this document.
              </li>
              <li class="flex gap-2">
                <span class="mt-1 h-2 w-2 rounded-full bg-amber-500"></span>
                You can request access or open another document below.
              </li>
            </ul>
          </div>

          <div class="mt-6 flex flex-wrap gap-3">
            <button
              type="button"
              class="rounded-full bg-slate-900 px-5 py-2 text-sm font-medium text-white shadow-sm transition hover:-translate-y-0.5 hover:bg-slate-800"
              @click="requestAccess"
            >
              Request access
            </button>
            <button
              type="button"
              class="rounded-full border border-slate-900/15 bg-white/80 px-5 py-2 text-sm font-medium text-slate-900 shadow-sm transition hover:-translate-y-0.5 hover:border-slate-900/30"
              @click="goHome"
            >
              Create a new document
            </button>
            <button
              type="button"
              class="rounded-full border border-slate-900/15 bg-white/50 px-5 py-2 text-sm font-medium text-slate-700 shadow-sm transition hover:-translate-y-0.5 hover:border-slate-900/30"
              @click="goLogin"
            >
              Login with another account
            </button>
          </div>

          <p v-if="requestStatus" class="mt-3 text-xs text-slate-600">
            {{ requestStatus }}
          </p>
          <div v-if="requestMessage" class="mt-3 rounded-2xl border border-slate-200/70 bg-white/80 p-3">
            <p class="text-[0.65rem] uppercase tracking-[0.2em] text-slate-500">Request message</p>
            <p class="mt-2 text-xs text-slate-700 break-words">{{ requestMessage }}</p>
          </div>

          <div class="mt-8 grid gap-4 sm:grid-cols-2">
            <div class="rounded-2xl border border-slate-900/10 bg-white/80 p-4 shadow-sm">
              <p class="text-xs uppercase tracking-[0.2em] text-slate-500">Document</p>
              <p class="mt-2 text-sm text-slate-700">
                <span v-if="documentId">ID {{ documentId }}</span>
                <span v-else>Unknown or hidden</span>
              </p>
            </div>
            <div class="rounded-2xl border border-slate-900/10 bg-white/80 p-4 shadow-sm">
              <p class="text-xs uppercase tracking-[0.2em] text-slate-500">Requested path</p>
              <p class="mt-2 text-sm text-slate-700 break-all">
                <span v-if="fromPath">{{ fromPath }}</span>
                <span v-else>Not available</span>
              </p>
            </div>
          </div>
        </section>

        <aside class="rounded-3xl border border-slate-900/10 bg-white/70 p-6 shadow-sm backdrop-blur">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-xs uppercase tracking-[0.2em] text-slate-500">Your documents</p>
              <h2 class="mt-2 text-xl font-semibold text-slate-900">Open another document</h2>
            </div>
            <button
              type="button"
              class="rounded-full border border-slate-900/15 bg-white/80 px-3 py-1 text-xs font-medium text-slate-700 transition hover:border-slate-900/30"
              @click="loadDocuments"
            >
              Refresh
            </button>
          </div>

          <div class="mt-4 space-y-3">
            <div v-if="documentsLoading" class="space-y-2">
              <div class="h-10 rounded-2xl bg-slate-200/70 animate-pulse"></div>
              <div class="h-10 rounded-2xl bg-slate-200/70 animate-pulse"></div>
              <div class="h-10 rounded-2xl bg-slate-200/70 animate-pulse"></div>
            </div>

            <p v-else-if="documentsError" class="rounded-2xl border border-rose-200/60 bg-rose-50/80 p-3 text-sm text-rose-700">
              {{ documentsError }}
            </p>

            <p v-else-if="documents.length === 0" class="rounded-2xl border border-slate-200/60 bg-white/80 p-3 text-sm text-slate-600">
              You do not have any documents yet.
            </p>

            <div v-else class="space-y-2">
              <button
                v-for="doc in documents"
                :key="doc.id"
                type="button"
                class="flex w-full items-center justify-between rounded-2xl border border-slate-900/10 bg-white/90 px-4 py-3 text-left text-sm text-slate-800 shadow-sm transition hover:-translate-y-0.5 hover:border-slate-900/25"
                @click="openDocument(doc.id)"
              >
                <span class="font-medium">Document #{{ doc.id }}</span>
                <span
                  class="rounded-full px-2 py-0.5 text-xs font-semibold"
                  :class="doc.publicAccess ? 'bg-emerald-100 text-emerald-700' : 'bg-amber-100 text-amber-700'"
                >
                  {{ doc.publicAccess ? 'Public' : 'Private' }}
                </span>
              </button>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const documents = ref([])
const documentsLoading = ref(false)
const documentsError = ref('')
const requestStatus = ref('')
const requestMessage = ref('')

const fromPath = computed(() => (typeof route.query.from === 'string' ? route.query.from : ''))

const documentId = computed(() => {
  if (!fromPath.value) {
    return ''
  }

  const match = fromPath.value.match(/\/document\/([^/?#]+)/)
  return match ? match[1] : ''
})

const usernameLabel = computed(() => authStore.username || 'your account')

function goHome() {
  router.push('/document/new')
}

function goLogin() {
  const redirectTarget = fromPath.value || '/document/new'
  router.push({
    path: '/login',
    query: { redirect: redirectTarget },
  })
}

function openDocument(id) {
  router.push(`/document/${id}`)
}

async function loadDocuments() {
  documentsLoading.value = true
  documentsError.value = ''

  try {
    documents.value = await authStore.listDocuments()
  } catch (err) {
    documentsError.value = err?.message || 'Unable to load your documents.'
  } finally {
    documentsLoading.value = false
  }
}

async function requestAccess() {
  const targetId = documentId.value || 'unknown'
  const message = `Hi, I need access to document ${targetId}. Please share it with ${usernameLabel.value}.`
  requestMessage.value = message

  try {
    if (navigator?.clipboard?.writeText) {
      await navigator.clipboard.writeText(message)
      requestStatus.value = 'Request message copied to your clipboard. Send it to the document owner.'
    } else {
      requestStatus.value = 'Copy this request message and send it to the document owner.'
    }
  } catch {
    requestStatus.value = 'Copy this request message and send it to the document owner.'
  }
}

onMounted(() => {
  loadDocuments()
})
</script>
