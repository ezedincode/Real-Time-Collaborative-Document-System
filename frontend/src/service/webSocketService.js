import { watch } from "vue";
import { useStateStore } from "../stores/state";
const stateStore = useStateStore();
const socket = new SockJs("http://localhost:8080/ws");
const stompClient = Stomp.over(socket);
stompClient.connect({}, () => {
    stompClient.subscribe("/topic/document/{id}", callback)
    });

watch(() => stateStore.documetEvent, (newValue) => {
    stompClient.send("/app//edit", {}, JSON.stringify(newValue));
})

function callback(message) {
    const documentEvent = JSON.parse(message.body);
    stateStore.documetEvent = documentEvent;
}
stompClient.subscribe("/topic/document/{id}", (message) => {
    const documentEvent = JSON.parse(message.body);
    stateStore.documetEvent = documentEvent;
});