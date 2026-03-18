import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { guestOnly: true },
  },
  {
    path: '/document/:id',
    name: 'document-editor',
    component: () => import('../views/DocumentEditorView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/',
    redirect: '/document/new',
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)
  const guestOnly = to.matched.some((record) => record.meta.guestOnly)
  const hasValidSession = authStore.hasValidSession()

  if (requiresAuth && !hasValidSession) {
    return {
      path: '/login',
      query: { redirect: to.fullPath },
    }
  }

  if (guestOnly && hasValidSession) {
    return '/document/new'
  }

  return true
})

export default router
