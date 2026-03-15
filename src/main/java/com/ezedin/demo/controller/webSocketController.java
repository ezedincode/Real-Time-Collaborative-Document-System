package com.ezedin.demo.controller;

import com.ezedin.demo.Document.documentEvent;
import com.ezedin.demo.Document.documentService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class webSocketController {
    private final documentService service;
    @MessageMapping("/edit")
    @SendTo("/topic/document")
    public documentEvent proccessor (documentEvent event){

        System.out.println(event);
        service.save(event);
        return event;
    }

}
