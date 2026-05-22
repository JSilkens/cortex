package be.jsilkens.cortex.common.domain.event;


public interface DomainEventPublisher {

    void publish(DomainEvent domainEvent);
}
