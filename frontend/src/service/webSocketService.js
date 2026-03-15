import SockJS from "sockjs-client";
import Stomp from "stompjs";

let stompClient = null;

export function connectWebSocket(onDocumentEvent) {
    const socket = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        stompClient.subscribe("/topic/document", (message) => {
            if (!onDocumentEvent) {
                return;
            }
            const documentEvent = JSON.parse(message.body);
            onDocumentEvent(documentEvent);
        });
    });
}

export function sendDocumentEvent(event) {
    if (!stompClient || !stompClient.connected) {
        return;
    }
    stompClient.send("/app/edit", {}, JSON.stringify(event));
}

export function disconnectWebSocket() {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect();
        stompClient = null;
    }
}