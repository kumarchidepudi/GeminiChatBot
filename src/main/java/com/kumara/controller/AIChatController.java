package com.kumara.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AIChatController {

    private final ChatClient geminiClient;
    private final ChatClient ollamaClient;

    public AIChatController(ChatClient ollamaClient, ChatClient geminiClient) {
        this.ollamaClient = ollamaClient;
        this.geminiClient = geminiClient;
    }

    /**
     * Simple chatbot implementation using Gemini API
     *
     * @return String response from the bot
     */
    @GetMapping("/chat/{model}")
    public String chat(@PathVariable String model) {
        Prompt prompt = new Prompt();
        prompt.mutate().chatOptions(ChatOptions
                .builder()
                .model("gemini-1.5-pro")
                .temperature(1.0)
                .maxTokens(100)
                .build());
        prompt.augmentUserMessage((message) ->
                UserMessage
                        .builder()
                        .media(Media.builder().mimeType(MimeType.valueOf("*")).build())
                        .text("This is my first ever chat bot using Gemini API")
                        .build());

        return getClient(model)
                .prompt(prompt)
                .call()
                .content();
    }

    /**
     * Chatbot implementation with session history using Gemini API
     *
     * @param prompt  User input prompt
     * @param session HttpSession to maintain chat history
     * @return String response from the bot
     */
    @GetMapping("/chat/{model}/{prompt}")
    public String chatWithPrompt(@PathVariable String prompt, HttpSession session, @PathVariable String model) {
        List<String> chatHistory = getHistory(session);
        StringBuilder stringBuilder = new StringBuilder();
        for (String chat : chatHistory) {
            stringBuilder.append(chat).append("\n");
        }
        String responseFromTheBot = getClient(model).prompt()
                .user(prompt)
                .system("You are an concise and precise assistant.")
                .system("Keep your answers short and to the point.")
                .system("CHAT_HISTORY \n" + stringBuilder)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
        chatHistory.add("User: " + prompt);
        chatHistory.add("Bot: " + responseFromTheBot);
        session.setAttribute("chatHistory", chatHistory);
        return responseFromTheBot;
    }

    /**
     * Retrieve chat history from the session.
     * @param session HttpSession to get chat history
     * @return List of chat history strings
     */
    private List<String> getHistory(HttpSession session) {
        return session.getAttribute("chatHistory") != null ?
                (List<String>) session.getAttribute("chatHistory") : new ArrayList<>();
    }

    /**
     * Get the appropriate ChatClient based on the model name.
     * @param model Model name ("gemini" or other)
     * @return Corresponding ChatClient
     */
    private ChatClient getClient(String model) {
        if (model.equals("gemini")) {
            return geminiClient;
        } else {
            return ollamaClient;
        }
    }
}


