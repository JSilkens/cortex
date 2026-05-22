# ADR-0001: Spring AI for LLM Integration

| Field       | Value                          |
|-------------|--------------------------------|
| Status      | Accepted                       |
| Date        | 2026-05-22                     |
| Author      | Johan Silkens                  |
| Issue       | JSilkens/cortex#3              |

## Context

Cortex needs a pluggable LLM service layer that can call local LLM backends (Ollama, vLLM, TensorRT-LLM, NVIDIA NIM) while keeping the domain and use-case modules framework-free. The adapter must be swappable without changing business logic.

Two mature Java frameworks were evaluated:

| Framework    | Version   | Spring Boot 4 Support | Approach                          |
|--------------|-----------|------------------------|-----------------------------------|
| Spring AI    | 2.0.0-M6  | Native (built for Boot 4) | Spring-native, auto-configured    |
| LangChain4j  | 1.0 GA    | Via separate starter   | Framework-agnostic, agent-focused |

## Decision

We adopt **Spring AI 2.0** (`spring-ai-starter-model-ollama`) as the LLM integration layer.

### Rationale

1. **Native Spring Boot 4.0 alignment** — Spring AI 2.0 is built on Spring Boot 4.0 and Spring Framework 7.0 with a Jakarta EE 11 baseline. No compatibility shims or version conflicts.

2. **Auto-configuration matches our pattern** — The `spring.ai.ollama.*` property prefix provides the same configuration-driven backend selection we need, fitting naturally into `application.yml` without custom `@ConfigurationProperties` boilerplate.

3. **Unified `ChatModel` interface** — Spring AI's `ChatModel` is already the pluggable abstraction. Swapping from Ollama to an OpenAI-compatible endpoint (vLLM, NIM) requires only a dependency and config change, no code changes in the adapter.

4. **Reduced custom code** — Instead of hand-rolling a RestClient-based Ollama adapter with manual JSON mapping, timeout handling, and error translation, we delegate that to Spring AI's battle-tested `OllamaChatModel`.

5. **Future-proof for Phase 2+** — Spring AI includes vector store integrations (Qdrant, Weaviate), RAG support, tool calling, and structured outputs — all on Cortex's roadmap.

6. **Hexagonal architecture preserved** — The `LlmPort` interface in `cortex-usecase` remains framework-free. The adapter in `cortex-llm` wraps Spring AI's `ChatModel` behind that port, maintaining the architectural boundary.

### Why Not LangChain4j

- **Overkill for Phase 1** — LangChain4j's strength is agentic orchestration (chains, memory, RAG pipelines). Cortex Phase 1 only needs simple prompt → completion.
- **Additional abstraction layer** — LangChain4j sits on top of Spring, adding its own programming model. With Spring AI, we stay within a single consistent ecosystem.
- **Less idiomatic** — LangChain4j's API style (builder-heavy, callback-oriented) doesn't align as naturally with Spring Boot conventions and auto-configuration.
- **Duplicate capabilities** — Once Cortex reaches Phase 2 (RAG, vector DB), Spring AI already provides those integrations natively. LangChain4j would duplicate what Spring AI offers.

### Integration Architecture

```
cortex-usecase (pure Java)
├── LlmPort (interface)
├── LlmRequest (record)
└── LlmResponse (sealed interface)

cortex-llm (Spring adapter)
├── SpringAiLlmAdapter (implements LlmPort, delegates to ChatModel)
├── MockLlmAdapter (implements LlmPort, for development)
└── LlmAdapterConfiguration (@Configuration, conditional bean wiring)

application.yml
└── spring.ai.ollama.* (base-url, model, options.temperature)
```

### Dependencies

```xml
<!-- cortex-llm/pom.xml -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
```

With BOM in root POM:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-bom</artifactId>
    <version>2.0.0-M6</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

## Consequences

- **Positive:** Less custom code, native Spring Boot 4 support, built-in Ollama/OpenAI-compatible client, clear upgrade path for RAG and vector DB in Phase 2, consistent ecosystem.
- **Negative:** Dependency on a pre-GA milestone (2.0.0-M6) until the May 28 GA release. Minor API changes possible between milestones.
- **Mitigation:** The `LlmPort` abstraction insulates use cases from Spring AI API changes. Only the adapter in `cortex-llm` would need updating if the Spring AI API shifts.
