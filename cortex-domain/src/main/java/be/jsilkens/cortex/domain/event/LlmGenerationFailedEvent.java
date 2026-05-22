package be.jsilkens.cortex.domain.event;

import be.jsilkens.cortex.common.domain.event.DomainEvent;
import be.jsilkens.cortex.common.domain.event.DomainEventType;

public record LlmGenerationFailedEvent(String prompt, String model, String errorMessage) implements DomainEvent {

    public LlmGenerationFailedEvent {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt must not be blank");
        }
        if (errorMessage == null || errorMessage.isBlank()) {
            throw new IllegalArgumentException("Error message must not be blank");
        }
    }

    @Override
    public DomainEventType getType() {
        return LlmEventType.LLM_GENERATION_FAILED;
    }

    @Override
    public Object getPayload() {
        return this;
    }
}
