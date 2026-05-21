package be.jsilkens.cortex.common.domain.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule aggregator that collects {@link ValidationRule}s and executes them
 * against an object, producing an {@link Outcome}.
 *
 * <p>Usage follows a fluent builder pattern:</p>
 * <pre>{@code
 * Outcome<MyObject> result = new Validator<MyObject>()
 *     .addRule((obj, errors) -> {
 *         if (obj.getName() == null) errors.add("Name is required");
 *     })
 *     .addRule((obj, errors) -> {
 *         if (obj.getAge() < 0) errors.add("Age must be non-negative");
 *     })
 *     .validate(myObject);
 * }</pre>
 *
 * @param <T> the type of object being validated
 */
public class Validator<T> {

    private final List<ValidationRule<T>> rules = new ArrayList<>();

    /**
     * Adds a validation rule to this validator.
     *
     * @param rule the rule to add
     * @return this validator for fluent chaining
     * @throws IllegalArgumentException if rule is null
     */
    public Validator<T> addRule(ValidationRule<T> rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Validation rule must not be null");
        }
        rules.add(rule);
        return this;
    }

    /**
     * Validates the given item against all registered rules.
     *
     * <p>All rules are executed regardless of individual failures, collecting
     * all error messages into a single result.</p>
     *
     * @param item the object to validate
     * @return {@link Outcome.Success} if no errors, {@link Outcome.Failure} with all errors otherwise
     */
    public Outcome<T> validate(T item) {
        List<String> errors = new ArrayList<>();
        for (ValidationRule<T> rule : rules) {
            rule.validate(item, errors);
        }
        if (errors.isEmpty()) {
            return new Outcome.Success<>(item);
        }
        return new Outcome.Failure<>(errors);
    }
}
