# Cortex

Privacy-first, ASD-friendly AI copilot for engineering work. Runs locally on DGX-class hardware.

## What is this?

Cortex is a personal AI operating system for engineering work. It reduces cognitive load, eliminates ambiguity, and provides predictable structure for task planning, meeting analysis, and stakeholder communication.

## Architecture

```
[ CLI / Web UI ]
       ↓
[ Spring Boot API ]
       ↓
[ Orchestration / Agents ]
       ↓
[ Local LLM (DGX) ]
       ↓
[ Memory: Vector DB + File Storage ]
```

## SLC Phases

| Phase | Goal | Key Addition |
|-------|------|---------------|
| 1 | Usable tool in days | Summarization, task extraction, daily planning |
| 2 | System remembers context | Vector DB, RAG, stakeholder memory |
| 3 | Autonomous workflows | Agent layer, tool selection, orchestration |
| 4 | Specialized agents | Planner, Meeting, Knowledge, Risk agents |
| 5 | Full daily automation | Morning plans, auto-processing, end-of-day reflection |

## Tech Stack

| Layer | Technology |
|-------|------------|
| LLM runtime | NVIDIA stack (vLLM / Ollama / TensorRT-LLM / NIM) |
| API | Spring Boot |
| Vector DB | Qdrant or Weaviate |
| Agent framework | LangChain → custom |
| UI | CLI first, web UI later |

## Design Principles

### ASD-Friendly by Design

- **Predictable structure** — Every response follows the same format, every time
- **Explicit over implicit** — Unknowns and ambiguity are always surfaced
- **Low cognitive load** — Bullet points, clear sections, no filler
- **Reduced social guesswork** — Stakeholder memory tracks preferences and patterns
- **Deterministic outputs** — Low temperature LLM for consistent responses
- **No context switching tax** — One system for planning, meetings, and knowledge

## Getting Started

Phase 1 implementation coming soon.

## License

Private project.
