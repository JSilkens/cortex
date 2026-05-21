package be.jsilkens.cortex.common.domain.validation;

/**
 * Marker interface for self-validating domain objects.
 *
 * <p>Domain objects implementing this interface declare that they can validate
 * their own invariants and return an {@link Outcome} indicating success or failure.</p>
 */
public interface Validatable {

    /**
     * Validates this object's invariants.
     *
     * @return an {@link Outcome} representing the validation result
     */
    Outcome<?> validate();
}
