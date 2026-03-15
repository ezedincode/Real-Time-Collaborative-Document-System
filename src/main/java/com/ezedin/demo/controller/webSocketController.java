package com.ezedin.demo.controller;

import com.ezedin.demo.Document.documentEvent;
import com.ezedin.demo.Document.documentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;

@Controller
@AllArgsConstructor
public class webSocketController {
    private final documentService service;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @MessageMapping("/edit")
    public void proccessor(documentEvent event) {
        System.out.println(event);
        service.save(event);
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize documentEvent for Kafka: " + e.getMessage());
            return;
        }

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("my-topic", payload);
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
