package com.ezedin.demo.controller;

import com.ezedin.demo.Document.documentEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class webSocketController {

    @MessageMapping("/app/edit")
    @SendTo("my-topic")
    public documentEvent proccessor (documentEvent event){
        return event;
    }

}
