package ai.query.q_ai.controller;

import ai.query.q_ai.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    ChatService service;

    @GetMapping("/chat")
    public String getChatResponse(@RequestParam("prompt") String prompt){
        return service.getChatResponse(prompt);
    }

}
