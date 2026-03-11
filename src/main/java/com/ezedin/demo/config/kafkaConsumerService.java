package com.ezedin.demo.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class kafkaConsumerService {

    @KafkaListener(topics = "my-topic", groupId = "document-group")
    public void listen(String message) {
        System.out.println("Consumed message: " + message);
    }
}