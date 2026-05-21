# Project Structure

Multi-module Maven project following hexagonal architecture (ports & adapters).

## Module Dependency Flow

```
common-domain  ←  cortex-domain  ←  cortex-usecase  ←  adapters (api, llm, storage)
                                                              ↓
                                                         application (wires everything)
```

## Modules

| Module           | Role                                                        | Framework Allowed |
|------------------|-------------------------------------------------------------|-------------------|
| `common-domain`  | Shared domain primitives (validation library)               | No                |
| `common-adapter` | Shared adapter utilities                                    | Yes               |
| `cortex-domain`  | Core domain models and repository port interfaces           | No                |
| `cortex-usecase` | Application use cases and port definitions                  | No                |
| `cortex-api`     | Inbound REST adapter (HTTP → use case invocations)          | Yes (Spring Web)  |
| `cortex-llm`     | Outbound LLM adapter (implements LlmPort)                   | Yes (Spring)      |
| `cortex-storage` | Outbound file storage adapter (implements StoragePort)      | Yes (Spring)      |
| `cortex-ui`      | Angular frontend, built via frontend-maven-plugin           | N/A (Node/Angular)|
| `application`    | Spring Boot assembly — wires all adapters, runs the app     | Yes               |

## Package Convention

Base package: `be.jsilkens.cortex`

- `be.jsilkens.cortex.common.domain..` — shared domain code
- `be.jsilkens.cortex.common.adapter..` — shared adapter code
- `be.jsilkens.cortex.domain..` — core domain models
- `be.jsilkens.cortex.usecase..` — use cases and ports
- `be.jsilkens.cortex.api..` — REST controllers
- `be.jsilkens.cortex.llm..` — LLM adapter
- `be.jsilkens.cortex.storage..` — storage adapter

## Architecture Rules (enforced by ArchUnit)

1. Domain and use-case packages must NOT depend on Spring.
2. Adapter packages (api, llm, storage) must NOT depend directly on `cortex-domain` — they go through `cortex-usecase`.
3. Domain and use-case packages must NOT depend on adapter packages.

## Test Location

Each module has its own `src/test/java` directory. Architecture tests live in the `application` module since it has visibility over all packages.
