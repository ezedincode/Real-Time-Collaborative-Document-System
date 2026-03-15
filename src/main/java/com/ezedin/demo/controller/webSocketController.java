package com.ezedin.demo.controller;

import com.ezedin.demo.Document.documentEvent;
import com.ezedin.demo.Document.documentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;

@Controller
@AllArgsConstructor
public class webSocketController {
    private final documentService service;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @MessageMapping("/document/{documentId}/edit")
    public void proccessor(@DestinationVariable Long documentId, documentEvent event, Principal principal) {
        if (principal == null) {
            return;
        }

        if (event.getId() == null) {
            event.setId(documentId);
        }

        if (!documentId.equals(event.getId())) {
            throw new IllegalArgumentException("Path document id does not match payload id");
        }

        documentEvent savedEvent = service.save(event, principal.getName());

        String payload;
        try {
            payload = objectMapper.writeValueAsString(savedEvent);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize documentEvent for Kafka: " + e.getMessage());
            return;
        }

        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send("my-topic", documentId.toString(), payload);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("Failed to send to Kafka: " + ex.getMessage());
                ex.printStackTrace();
            } else if (result != null) {
                System.out.println("Sent to Kafka topic 'my-topic': " + result.getRecordMetadata());
            }
        });
    }

}
