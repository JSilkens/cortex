# Tech Stack

## Core

| Concern        | Technology                              |
|----------------|------------------------------------------|
| Language       | Java 21                                  |
| Framework      | Spring Boot 4.0.x                        |
| Build          | Maven (multi-module, wrapper included)   |
| Architecture   | Hexagonal / Ports & Adapters             |
| Frontend       | Angular (built via frontend-maven-plugin)|
| LLM integration| Spring AI 2.0 (see ADR-0001)            |
| LLM runtime    | Ollama / vLLM / TensorRT-LLM / NIM      |
| Vector DB      | Qdrant or Weaviate (planned)             |

## Key Libraries

- Spring AI 2.0 (`spring-ai-starter-model-ollama` for LLM integration)
- Lombok (compile-time annotation processing)
- ArchUnit (architecture rule enforcement in tests)
- JUnit 5 + AssertJ + Mockito (testing)
- Instancio (property-based test data generation)
- Spring Boot Starter Web, Actuator

## Common Commands

```bash
# Full build (compile + test all modules including Angular)
./mvnw clean verify

# Build without tests
./mvnw clean package -DskipTests

# Run tests only
./mvnw test

# Run a single module's tests
./mvnw test -pl cortex-domain

# Start the application
./mvnw spring-boot:run -pl application

# Skip the frontend build (useful during backend-only work)
./mvnw clean verify -pl !cortex-ui
```

## Notes

- The Maven wrapper (`mvnw`) is checked in — always use `./mvnw` rather than a system-installed `mvn`.
- Domain and use-case modules must remain framework-free (no Spring dependencies). This is enforced by ArchUnit tests.
- Lombok annotation processing is configured in the parent POM's `maven-compiler-plugin`.
