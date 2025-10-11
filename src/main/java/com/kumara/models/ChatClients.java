package com.kumara.models;

import com.google.genai.Client;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClients {

    @Value("${my-api}")
    String myApi;

    /**
     * Gemini Chat Client Bean
     *
     * @return ChatClient configured for Gemini model
     */
    @Bean
    public ChatClient geminiClient() {
        return ChatClient
                .builder(GoogleGenAiChatModel
                        .builder()
                        .defaultOptions(GoogleGenAiChatOptions
                                .builder()
                                .model(GoogleGenAiChatModel.ChatModel.GEMINI_2_5_FLASH_LIGHT)
                                .build())
                        .genAiClient(Client
                                .builder()
                                .apiKey(myApi)
                                .build())
                        .build())
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                        .builder(MessageWindowChatMemory.builder().build())
                        .build()
                )
                .build();
    }

    /**
     * Ollama Chat Client Bean
     *
     * @return ChatClient configured for Ollama model
     */
    @Bean("ollamaClient")
    public ChatClient ollamaClient() {
        return ChatClient
                .builder(OllamaChatModel
                        .builder()
                        .ollamaApi(OllamaApi
                                .builder()
                                .baseUrl("http://localhost:11434")
                                .build())
                        .defaultOptions(OllamaChatOptions
                                .builder()
                                .model(OllamaModel.GEMMA3)
                                .build())
                        .build()
                ).build();
    }
}
