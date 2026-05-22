package be.jsilkens.cortex.domain;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.domain.testdata.LlmRequestTestdata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LlmRequest")
class LlmRequestTest {

    @DisplayName("GIVEN valid fields WHEN validating THEN Outcome.Success")
    @Test
    void givenValidFields_whenValidating_thenOutcomeSuccess() {
        var subject = LlmRequestTestdata.fullTestData();

        Outcome<LlmRequest> actual = subject.validate();

        assertThat(actual).isEqualTo(new Outcome.Success<>(subject));
    }

    @DisplayName("GIVEN blank prompt WHEN validating THEN Outcome.Failure with prompt error")
    @Test
    void givenBlankPrompt_whenValidating_thenOutcomeFailureWithPromptError() {
        var subject = LlmRequestTestdata.validBuilder()
                .prompt("   ")
                .build();

        Outcome<LlmRequest> actual = subject.validate();

        assertThat(actual).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<LlmRequest>) actual).errors())
                .contains("Prompt must not be blank");
    }

    @DisplayName("GIVEN null prompt WHEN validating THEN Outcome.Failure with prompt error")
    @Test
    void givenNullPrompt_whenValidating_thenOutcomeFailureWithPromptError() {
        var subject = LlmRequestTestdata.validBuilder()
                .prompt(null)
                .build();

        Outcome<LlmRequest> actual = subject.validate();

        assertThat(actual).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<LlmRequest>) actual).errors())
                .contains("Prompt must not be blank");
    }

    @DisplayName("GIVEN blank model WHEN validating THEN Outcome.Failure with model error")
    @Test
    void givenBlankModel_whenValidating_thenOutcomeFailureWithModelError() {
        var subject = LlmRequestTestdata.validBuilder()
                .model("   ")
                .build();

        Outcome<LlmRequest> actual = subject.validate();

        assertThat(actual).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<LlmRequest>) actual).errors())
                .contains("Model must not be blank");
    }

    @DisplayName("GIVEN temperature below 0.0 WHEN validating THEN Outcome.Failure with temperature error")
    @Test
    void givenTemperatureBelowZero_whenValidating_thenOutcomeFailureWithTemperatureError() {
        var subject = LlmRequestTestdata.validBuilder()
                .temperature(-0.1)
                .build();

        Outcome<LlmRequest> actual = subject.validate();

        assertThat(actual).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<LlmRequest>) actual).errors())
                .contains("Temperature must be between 0.0 and 2.0 inclusive");
    }

    @DisplayName("GIVEN temperature above 2.0 WHEN validating THEN Outcome.Failure with temperature error")
    @Test
    void givenTemperatureAboveTwo_whenValidating_thenOutcomeFailureWithTemperatureError() {
        var subject = LlmRequestTestdata.validBuilder()
                .temperature(2.1)
                .build();

        Outcome<LlmRequest> actual = subject.validate();

        assertThat(actual).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<LlmRequest>) actual).errors())
                .contains("Temperature must be between 0.0 and 2.0 inclusive");
    }

    @DisplayName("GIVEN maxTokens of 0 WHEN validating THEN Outcome.Failure with maxTokens error")
    @Test
    void givenMaxTokensZero_whenValidating_thenOutcomeFailureWithMaxTokensError() {
        var subject = LlmRequestTestdata.validBuilder()
                .maxTokens(0)
                .build();

        Outcome<LlmRequest> actual = subject.validate();

        assertThat(actual).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<LlmRequest>) actual).errors())
                .contains("Max tokens must be positive");
    }

    @DisplayName("GIVEN negative maxTokens WHEN validating THEN Outcome.Failure with maxTokens error")
    @Test
    void givenNegativeMaxTokens_whenValidating_thenOutcomeFailureWithMaxTokensError() {
        var subject = LlmRequestTestdata.validBuilder()
                .maxTokens(-10)
                .build();

        Outcome<LlmRequest> actual = subject.validate();

        assertThat(actual).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<LlmRequest>) actual).errors())
                .contains("Max tokens must be positive");
    }

    @DisplayName("GIVEN all fields invalid WHEN validating THEN Outcome.Failure with all error messages")
    @Test
    void givenAllFieldsInvalid_whenValidating_thenOutcomeFailureWithAllErrors() {
        var subject = LlmRequest.builder()
                .prompt("")
                .model("")
                .temperature(3.0)
                .maxTokens(0)
                .build();

        Outcome<LlmRequest> actual = subject.validate();

        assertThat(actual).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<LlmRequest>) actual).errors())
                .contains("Prompt must not be blank")
                .contains("Model must not be blank")
                .contains("Temperature must be between 0.0 and 2.0 inclusive")
                .contains("Max tokens must be positive");
    }
}
