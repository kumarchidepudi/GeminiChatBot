# GeminiChatBot

A minimal Spring Boot demo that uses Spring AI's Google GenAI (Gemini) model to provide simple chatbot endpoints.

This repository demonstrates how to call the Google GenAI (Gemini) model using Spring AI and expose two HTTP endpoints: a basic chat endpoint and a chat endpoint that preserves a simple per-session conversation history.

---

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Configuration](#configuration)
- [Build & Run](#build--run)
- [Endpoints](#endpoints)
- [Session / Cookie Behavior](#session--cookie-behavior)
- [Development & Tests](#development--tests)
- [Troubleshooting](#troubleshooting)
- [Project Structure](#project-structure)
- [License](#license)

---

## Features

- Java 21 and Spring Boot 3.5.x
- Uses `spring-ai-starter-model-google-genai` to call Gemini (`gemini-2.0-flash-lite-001`) via Spring AI
- Two simple REST endpoints:
    - `GET /chat` — one-off prompt, returns the model reply
    - `GET /chat/{prompt}` — preserves chat history in `HttpSession` and forwards it to the model
- Demonstrates basic use of `ChatClient` from Spring AI

## Requirements

- Java 21 (JDK 21)
- Maven (3.x)
- A Google GenAI API key with access to the chosen Gemini model

## Configuration

The app reads configuration from `src/main/resources/application.properties`. Key properties used in this project:

- `spring.ai.google.genai.api-key` — the API key value is supplied via the `${my-api}` property placeholder in the shipped `application.properties`.
- `spring.ai.google.genai.chat.options.model` — the Gemini model to use (default in this project: `gemini-2.0-flash-lite-001`).
- Session cookie settings such as `server.servlet.session.cookie.http-only` and `server.servlet.session.cookie.path` are configured in `application.properties`.

Example `src/main/resources/application.properties` (from this project):

```ini
spring.application.name=GeminiChatBot
spring.ai.google.genai.api-key=${my-api}
spring.ai.google.genai.chat.options.model=gemini-2.0-flash-lite-001
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.path=/chat
```

How to provide the API key at runtime:

- Option 1 — as a JVM system property:

```bash
mvn spring-boot:run -Dmy-api="YOUR_API_KEY_HERE"
# or when running the jar
java -Dmy-api="YOUR_API_KEY_HERE" -jar target/GeminiChatBot-0.0.1-SNAPSHOT.jar
```

- Option 2 — export as an environment variable and pass it to the JVM (example with macOS / zsh):

```bash
export MY_API=YOUR_API_KEY_HERE
mvn spring-boot:run -Dmy-api="$MY_API"
```

Note: The project uses a property placeholder named `my-api` in `application.properties`. Provide the value as a JVM property `-Dmy-api=...` or adapt the `application.properties` to read from a different environment variable if you prefer.

## Build & Run

Build the project with Maven:

```bash
mvn clean package
```

Run with Maven (convenient for development):

```bash
mvn spring-boot:run -Dmy-api="YOUR_API_KEY_HERE"
```

Run the built jar:

```bash
java -Dmy-api="YOUR_API_KEY_HERE" -jar target/GeminiChatBot-0.0.1-SNAPSHOT.jar
```

Optional: Build an OCI image using the Spring Boot Maven plugin (see the Spring Boot docs for `build-image`).

## Endpoints

1) GET /chat

- Description: Simple one-off prompt (hard-coded in the controller) that calls the Gemini model and returns the model text output.

Example:

```bash
curl -s http://localhost:8080/chat
```

2) GET /chat/{prompt}

- Description: Sends the `{prompt}` text to the Gemini model and includes a very small chat history stored in the server `HttpSession`. The controller stores a list of alternating "User:" and "Bot:" entries under the session attribute `chatHistory`.

Example:

```bash
curl -s "http://localhost:8080/chat/Hello%20there"
```

Notes:
- Responses are returned as plain text (the controller returns the `String` text result from the model).
- The chat history is purely for demo purposes and is appended to each prompt as a `system` message so the model can consider recent turns.

## Session / Cookie Behavior

The application configures the session cookie to be `HttpOnly` and to have a path of `/chat` so the cookie won't be sent for other paths by default. This demonstrates a simple server-side session-scoped chat history with basic cookie scoping.

Implications:
- `HttpOnly` prevents JavaScript from reading the cookie from the browser — an intentional security measure.
- `server.servlet.session.cookie.path=/chat` restricts the cookie to paths under `/chat`. Requests to other paths (for example `/login`) won't carry the cookie automatically.

## Development & Tests

Run the unit tests with Maven:

```bash
mvn test
```

The project includes a basic Spring Boot test skeleton under `src/test/java`.

## Troubleshooting

- 401 / Authorization errors:
    - Ensure the API key is set correctly and has access to the GenAI API.
    - Verify you're passing it as `-Dmy-api="YOUR_API_KEY"` or by editing `application.properties` directly.

- Model/network errors:
    - Check network connectivity and the GenAI service availability.

- Session not persisted between requests:
    - Confirm your client accepts cookies and that requests target the `/chat` path where the cookie is valid.

- If you change properties or the model version, restart the application to pick up the new configuration.

## Project Structure

- `src/main/java/com/kumara/GeminiChatBotApplication.java` — Spring Boot application entry point
- `src/main/java/com/kumara/controller/GeminiChatController.java` — controller exposing `/chat` endpoints and using `ChatClient`
- `src/main/resources/application.properties` — runtime configuration (API key placeholder, model selection, session cookie settings)
- `pom.xml` — Maven build configuration and dependencies

## Next steps / Improvements (optional)

- Add more robust conversation state management (persist history to a database, add max-history limits).
- Add authentication and rate-limiting for endpoints.
- Make the model and prompt templating configurable via properties or a small UI.
- Add request/response DTOs and JSON endpoints if you prefer structured responses instead of plain text.

## License

This repository contains example/demo code. Add a license that fits your needs (e.g. MIT, Apache-2.0). If you want, I can add a `LICENSE` file for you — tell me which license you prefer.
