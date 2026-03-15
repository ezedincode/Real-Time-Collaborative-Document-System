import { defineStore} from 'pinia'
import { ref } from 'vue'

export const useStateStore = defineStore('state', () => {
    const id = ref(0)
    const documentEvent = ref({
        id: 0,
        content: '',
        clientId: '',
    })

    return { id, documentEvent }
})
   