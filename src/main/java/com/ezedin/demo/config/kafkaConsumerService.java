package com.ezedin.demo.config;

import com.ezedin.demo.Document.documentEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class kafkaConsumerService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "my-topic", groupId = "document-group")
    public void listen(String value) throws JsonProcessingException {
        // See raw payload from Kafka
        System.out.println("RAW from Kafka: " + value);

        // Convert JSON string to documentEvent
        documentEvent message = objectMapper.readValue(value, documentEvent.class);
        System.out.println("Consumed message as documentEvent: " + message);

        // Forward to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/document", message);
    }
}