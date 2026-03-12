import { defineStore} from 'pinia'
import { ref } from 'vue'

export const useStateStore = defineStore('state', () => {
    const id = ref(0);
    const documetEvent = ref({
        id: 0,
        content: ""
    })

    return { id, documetEvent }
})
   