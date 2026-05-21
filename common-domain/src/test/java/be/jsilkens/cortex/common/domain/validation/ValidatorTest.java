package be.jsilkens.cortex.common.domain.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Validator")
class ValidatorTest {

    @Test
    @DisplayName("with no rules returns Success containing the validated item")
    void noRulesReturnsSuccess() {
        var validator = new Validator<String>();

        Outcome<String> result = validator.validate("hello");

        assertThat(result).isInstanceOf(Outcome.Success.class);
        assertThat(((Outcome.Success<String>) result).value()).isEqualTo("hello");
    }

    @Test
    @DisplayName("with all passing rules returns Success")
    void allPassingRulesReturnsSuccess() {
        var validator = new Validator<Integer>()
                .addRule((value, errors) -> {
                    if (value < 0) errors.add("Must be non-negative");
                })
                .addRule((value, errors) -> {
                    if (value > 100) errors.add("Must be at most 100");
                });

        Outcome<Integer> result = validator.validate(50);

        assertThat(result).isInstanceOf(Outcome.Success.class);
        assertThat(((Outcome.Success<Integer>) result).value()).isEqualTo(50);
    }

    @Test
    @DisplayName("with a failing rule returns Failure with error message")
    void failingRuleReturnsFailure() {
        var validator = new Validator<Integer>()
                .addRule((value, errors) -> {
                    if (value < 0) errors.add("Must be non-negative");
                });

        Outcome<Integer> result = validator.validate(-5);

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<Integer>) result).errors()).containsExactly("Must be non-negative");
    }

    @Test
    @DisplayName("aggregates errors from multiple failing rules")
    void aggregatesMultipleErrors() {
        var validator = new Validator<String>()
                .addRule((value, errors) -> {
                    if (value == null || value.isBlank()) errors.add("Must not be blank");
                })
                .addRule((value, errors) -> {
                    if (value != null && value.length() < 3) errors.add("Must be at least 3 characters");
                });

        Outcome<String> result = validator.validate("ab");

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<String>) result).errors())
                .containsExactly("Must be at least 3 characters");
    }

    @Test
    @DisplayName("collects errors from all rules even when multiple fail")
    void collectsAllErrors() {
        var validator = new Validator<Integer>()
                .addRule((value, errors) -> {
                    if (value < 10) errors.add("Must be at least 10");
                })
                .addRule((value, errors) -> {
                    if (value % 2 != 0) errors.add("Must be even");
                });

        Outcome<Integer> result = validator.validate(3);

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<Integer>) result).errors())
                .containsExactly("Must be at least 10", "Must be even");
    }

    @Test
    @DisplayName("addRule with null throws IllegalArgumentException")
    void addRuleNullThrowsException() {
        var validator = new Validator<String>();

        assertThatThrownBy(() -> validator.addRule(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }

    @Test
    @DisplayName("addRule returns the same validator for fluent chaining")
    void addRuleReturnsSameInstance() {
        var validator = new Validator<String>();

        Validator<String> returned = validator.addRule((value, errors) -> {});

        assertThat(returned).isSameAs(validator);
    }
}
