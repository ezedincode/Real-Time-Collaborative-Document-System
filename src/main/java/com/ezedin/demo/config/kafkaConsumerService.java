package com.ezedin.demo.config;

import com.ezedin.demo.Document.documentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class kafkaConsumerService {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "my-topic", groupId = "document-group")
    public void listen(documentEvent message) {
        Long documentId = message.getDocumentID();
        messagingTemplate.convertAndSend("my-topic/document/" + documentId, message);
        System.out.println("Consumed message: " + message);
    }
}