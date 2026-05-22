package be.jsilkens.cortex.common.domain.event;

public interface DomainEvent {

    DomainEventType getType();

    Object getPayload();
}
