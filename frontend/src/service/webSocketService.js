import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let stompClient = null;
let activeDocumentId = null;
let activeSubscription = null;

export function connectWebSocket({ token, documentId, onDocumentEvent, onError }) {
    stompClient = new Client({
        webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
        connectHeaders: { Authorization: `Bearer ${token}` },
        reconnectDelay: 3000,
    });

    activeDocumentId = documentId;

    stompClient.onConnect = () => {
        activeSubscription = stompClient.subscribe(`/topic/document/${documentId}`, (message) => {
            if (!onDocumentEvent) {
                return;
            }
            const documentEvent = JSON.parse(message.body);
            onDocumentEvent(documentEvent);
        });
    };

    stompClient.onWebSocketError = (error) => {
        if (onError) {
            onError(error);
        }
    };

    stompClient.onStompError = (frame) => {
        if (onError) {
            onError(frame);
        }
    };

    stompClient.onDisconnect = () => {
        if (activeSubscription) {
            activeSubscription.unsubscribe();
            activeSubscription = null;
        }
    };

    stompClient.activate();
}

export function sendDocumentEvent(event) {
    if (!stompClient || !stompClient.connected || activeDocumentId == null) {
        return;
    }
    stompClient.publish({
        destination: `/app/document/${activeDocumentId}/edit`,
        body: JSON.stringify(event),
    });
}

export function disconnectWebSocket() {
    if (activeSubscription) {
        activeSubscription.unsubscribe();
        activeSubscription = null;
    }

    if (stompClient) {
        stompClient.deactivate();
    }

    stompClient = null;
    activeDocumentId = null;
}