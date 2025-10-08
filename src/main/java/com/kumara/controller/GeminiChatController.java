package com.kumara.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeminiChatController {

    private final ChatClient chatClient;

    public GeminiChatController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/chat")
    public String chat() {
        return chatClient.prompt()
                .user("This is my first ever chat bot using Gemini API")
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }

    @GetMapping("/chat/{prompt}")
    public String chatWithPrompt(@PathVariable String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .system("You are an concise and precise assistant.")
                .system("Keep your answers short and to the point.")
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }
}
