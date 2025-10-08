package com.kumara.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GeminiChatController {

    private final ChatClient chatClient;

    public GeminiChatController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    /**
     * Simple chatbot implementation using Gemini API
     * @return String response from the bot
     */
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

    /**
     * Chatbot implementation with session history using Gemini API
     * @param prompt User input prompt
     * @param session HttpSession to maintain chat history
     * @return String response from the bot
     */
    @GetMapping("/chat/{prompt}")
    public String chatWithPrompt(@PathVariable String prompt, HttpSession session) {
        List<String> chatHistory = getHistory(session);
        StringBuilder stringBuilder = new StringBuilder();
        for (String chat : chatHistory) {
            stringBuilder.append(chat).append("\n");
        }
        String reponseFromTheBot = chatClient.prompt()
                .user(prompt)
                .system("You are an concise and precise assistant.")
                .system("Keep your answers short and to the point.")
                .system("CHAT_HISTORY \n" + stringBuilder)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
        chatHistory.add("User: "+prompt);
        chatHistory.add("Bot: "+reponseFromTheBot);
        session.setAttribute("chatHistory", chatHistory);
        return reponseFromTheBot;
    }

    private List<String> getHistory(HttpSession session) {
        List<String> chatHistory = session.getAttribute("chatHistory") != null ?
                (List<String>) session.getAttribute("chatHistory") : new ArrayList<>();
        return chatHistory;
    }
}


