package be.jsilkens.cortex.common.domain.validation;

import java.util.List;

/**
 * Functional interface representing a single validation rule.
 *
 * <p>A rule inspects an object and appends error messages to the provided list
 * if the object violates the rule's constraint.</p>
 *
 * @param <T> the type of object being validated
 */
@FunctionalInterface
public interface ValidationRule<T> {

    /**
     * Validates the given object, adding error messages to the list if validation fails.
     *
     * @param object the object to validate
     * @param errors the mutable list to which error messages should be added
     */
    void validate(T object, List<String> errors);
}
