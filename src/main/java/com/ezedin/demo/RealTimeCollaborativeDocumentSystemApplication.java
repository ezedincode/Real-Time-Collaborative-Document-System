package com.ezedin.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class RealTimeCollaborativeDocumentSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimeCollaborativeDocumentSystemApplication.class, args);
	}

}
