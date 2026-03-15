import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
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
  const token = localStorage.getItem('rtcds-access-token')

  if (to.meta.requiresAuth && !token) {
    return {
      path: '/login',
      query: { redirect: to.fullPath },
    }
  }

  if (to.path === '/login' && token) {
    return '/document/new'
  }

  return true
})

export default router
