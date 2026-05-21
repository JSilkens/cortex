package be.jsilkens.cortex.common.domain.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Sealed result type providing monadic error handling for validation outcomes.
 *
 * <p>An {@code Outcome} is either a {@link Success} containing a valid value,
 * or a {@link Failure} containing a list of error messages.</p>
 *
 * @param <T> the type of the validated value
 */
public sealed interface Outcome<T> permits Outcome.Success, Outcome.Failure {

    /**
     * Transforms the value inside a successful outcome.
     *
     * @param mapper the transformation function
     * @param <R>    the result type
     * @return a new {@code Outcome} with the mapped value, or the original failure
     */
    <R> Outcome<R> map(Function<T, R> mapper);

    /**
     * Chains a validation operation that itself returns an {@code Outcome}.
     *
     * @param mapper the function producing a new {@code Outcome}
     * @param <R>    the result type
     * @return the result of applying the mapper, or the original failure
     */
    <R> Outcome<R> flatMap(Function<T, Outcome<R>> mapper);

    /**
     * Converts this outcome to an {@link Optional}, discarding error information.
     *
     * @return {@code Optional.of(value)} for success, {@code Optional.empty()} for failure
     */
    Optional<T> toOptional();

    /**
     * Merges multiple outcomes into a single result. If all outcomes are successful,
     * returns {@code Success} with the provided value. If any outcome is a failure,
     * returns {@code Failure} with all error messages combined.
     *
     * @param value    the value to wrap in success if all outcomes pass
     * @param outcomes the outcomes to merge
     * @param <T>      the result type
     * @return a merged outcome
     */
    @SafeVarargs
    static <T> Outcome<T> merge(T value, Outcome<?>... outcomes) {
        List<String> errors = new ArrayList<>();
        for (Outcome<?> outcome : outcomes) {
            if (outcome instanceof Failure<?> failure) {
                errors.addAll(failure.errors());
            }
        }
        if (errors.isEmpty()) {
            return new Success<>(value);
        }
        return new Failure<>(errors);
    }

    /**
     * A successful validation outcome containing the validated value.
     *
     * @param value the validated value
     * @param <T>   the type of the value
     */
    record Success<T>(T value) implements Outcome<T> {

        @Override
        public <R> Outcome<R> map(Function<T, R> mapper) {
            return new Success<>(mapper.apply(value));
        }

        @Override
        public <R> Outcome<R> flatMap(Function<T, Outcome<R>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.ofNullable(value);
        }
    }

    /**
     * A failed validation outcome containing error messages.
     *
     * @param errors the list of validation error messages
     * @param <T>    the type that was being validated
     */
    record Failure<T>(List<String> errors) implements Outcome<T> {

        /**
         * Convenience constructor for a single error message.
         *
         * @param message the error message
         */
        public Failure(String message) {
            this(List.of(message));
        }

        /**
         * Compact constructor ensuring the errors list is unmodifiable.
         */
        public Failure {
            errors = Collections.unmodifiableList(new ArrayList<>(errors));
        }

        @Override
        public <R> Outcome<R> map(Function<T, R> mapper) {
            return new Failure<>(errors);
        }

        @Override
        public <R> Outcome<R> flatMap(Function<T, Outcome<R>> mapper) {
            return new Failure<>(errors);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }
    }
}
