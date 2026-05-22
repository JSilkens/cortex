package be.jsilkens.cortex.domain.event;

import be.jsilkens.cortex.common.domain.event.DomainEventType;

public enum LlmEventType implements DomainEventType {
    TEXT_GENERATED,
    LLM_GENERATION_FAILED;
}
