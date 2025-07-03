# 🤖 Spring AI Chatbot Application (Q-Ai)

This is a Spring Boot application built with Gradle and Java 21, utilizing the [Spring AI](https://docs.spring.io/spring-ai/reference/) library to integrate OpenAI’s Large Language Models (LLMs). It provides an API-ready backend for AI-driven features like chat, summarization, Q&A, and more.

---

## 📌 Project Details

| Feature                    | Description                                 |
|---------------------------|---------------------------------------------|
| Framework                 | Spring Boot 3.5.3                            |
| Language                  | Java 21                                      |
| Build Tool                | Gradle                                       |
| AI Integration            | Spring AI (`spring-ai-starter-model-openai`) |
| Test Framework            | JUnit (Platform Launcher)                    |
| Dependency Management     | Spring Dependency Management Plugin          |
| LLM Provider              | OpenAI                                       |

---

## 📁 Project Structure

spring-ai-chatbot/
├── src/
│ ├── main/
│ │ ├── java/ai/query/ # Java source files
│ │ └── resources/ # Configuration files (e.g., application.yml)
│ └── test/ # Test classes
├── build.gradle # Gradle build configuration
├── settings.gradle # Gradle settings
└── README.md # Project documentation


---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 21 installed
- Gradle installed or use the included `gradlew` wrapper
- OpenAI API Key ([Get yours here](https://platform.openai.com/account/api-keys))

### 📦 Clone the Repository

```bash
git clone https://github.com/vishesh0404/q-ai.git
cd q-ai

```

### Add Environment Variable

export OPENROUTER_API_KEY=<llm-model-api-key-here>


### Update application.yaml

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENROUTER_API_KEY}
      base_url: ${OPENAI_BASE_URL:https://openrouter.ai/api}
  chat:
    model: deepseek/deepseek-r1:free <any other model>

```

### Build

./gradlew clean build

### Running Tests

./gradlew test


