import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

const API_BASE = 'http://localhost:8080'
const TOKEN_KEY = 'rtcds-access-token'
const USERNAME_KEY = 'rtcds-username'
const USER_ID_KEY = 'rtcds-user-id'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(localStorage.getItem(TOKEN_KEY) || '')
  const username = ref(localStorage.getItem(USERNAME_KEY) || '')
  const userId = ref(localStorage.getItem(USER_ID_KEY) || '')

  const isAuthenticated = computed(() => !!accessToken.value)

  function persistAuth(response) {
    accessToken.value = response.accessToken
    username.value = response.username
    userId.value = String(response.userId)

    localStorage.setItem(TOKEN_KEY, accessToken.value)
    localStorage.setItem(USERNAME_KEY, username.value)
    localStorage.setItem(USER_ID_KEY, userId.value)
  }

  function clearAuth() {
    accessToken.value = ''
    username.value = ''
    userId.value = ''
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USERNAME_KEY)
    localStorage.removeItem(USER_ID_KEY)
  }

  async function requestAuth(path, payload) {
    const response = await fetch(`${API_BASE}${path}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    })

    if (!response.ok) {
      const message = await response.text()
      throw new Error(message || 'Authentication failed')
    }

    const json = await response.json()
    persistAuth(json)
    return json
  }

  async function register(usernameValue, passwordValue) {
    return requestAuth('/api/auth/register', {
      username: usernameValue,
      password: passwordValue,
    })
  }

  async function login(usernameValue, passwordValue) {
    return requestAuth('/api/auth/login', {
      username: usernameValue,
      password: passwordValue,
    })
  }

  async function createDocument(initialContent = '', publicAccess = false) {
    const response = await fetch(`${API_BASE}/api/documents`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken.value}`,
      },
      body: JSON.stringify({
        content: initialContent,
        publicAccess,
      }),
    })

    if (!response.ok) {
      const message = await response.text()
      throw new Error(message || 'Failed to create document')
    }

    return response.json()
  }

  async function getDocument(documentId) {
    const response = await fetch(`${API_BASE}/api/documents/${documentId}`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${accessToken.value}`,
      },
    })

    if (!response.ok) {
      const message = await response.text()
      throw new Error(message || 'Failed to load document')
    }

    return response.json()
  }

  return {
    accessToken,
    username,
    userId,
    isAuthenticated,
    clearAuth,
    register,
    login,
    createDocument,
    getDocument,
  }
})
