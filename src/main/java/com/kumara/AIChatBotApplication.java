package com.kumara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Gemini Chat Bot application.
 * This class bootstraps the Spring Boot application.
 * It is annotated with @SpringBootApplication to enable component scanning,
 * autoconfiguration, and property support.
 */
@SpringBootApplication
public class AIChatBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIChatBotApplication.class, args);
    }
}
