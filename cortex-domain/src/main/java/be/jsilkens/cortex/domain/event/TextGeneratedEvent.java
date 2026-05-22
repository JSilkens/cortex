package be.jsilkens.cortex.domain.event;

import be.jsilkens.cortex.common.domain.event.DomainEvent;
import be.jsilkens.cortex.common.domain.event.DomainEventType;

public record TextGeneratedEvent(String prompt, String model, String generatedText) implements DomainEvent {

    public TextGeneratedEvent {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt must not be blank");
        }
        if (generatedText == null || generatedText.isBlank()) {
            throw new IllegalArgumentException("Generated text must not be blank");
        }
    }

    @Override
    public DomainEventType getType() {
        return LlmEventType.TEXT_GENERATED;
    }

    @Override
    public Object getPayload() {
        return this;
    }
}
