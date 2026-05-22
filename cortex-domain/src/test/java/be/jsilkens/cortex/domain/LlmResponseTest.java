package be.jsilkens.cortex.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LlmResponse")
class LlmResponseTest {

    @Nested
    @DisplayName("Success")
    class SuccessTests {

        @DisplayName("GIVEN non-null text WHEN creating Success THEN record is created")
        @Test
        void givenNonNullText_whenCreatingSuccess_thenRecordIsCreated() {
            var subject = new LlmResponse.Success("Hello world");

            assertThat(subject.text()).isEqualTo("Hello world");
        }

        @DisplayName("GIVEN null text WHEN creating Success THEN IllegalArgumentException")
        @Test
        void givenNullText_whenCreatingSuccess_thenIllegalArgumentException() {
            assertThatThrownBy(() -> new LlmResponse.Success(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Response text must not be null");
        }
    }

    @Nested
    @DisplayName("Failure")
    class FailureTests {

        @DisplayName("GIVEN non-blank message WHEN creating Failure THEN record is created")
        @Test
        void givenNonBlankMessage_whenCreatingFailure_thenRecordIsCreated() {
            var subject = new LlmResponse.Failure("Connection timed out");

            assertThat(subject.errorMessage()).isEqualTo("Connection timed out");
        }

        @DisplayName("GIVEN blank message WHEN creating Failure THEN IllegalArgumentException")
        @Test
        void givenBlankMessage_whenCreatingFailure_thenIllegalArgumentException() {
            assertThatThrownBy(() -> new LlmResponse.Failure("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Error message must not be blank");
        }

        @DisplayName("GIVEN null message WHEN creating Failure THEN IllegalArgumentException")
        @Test
        void givenNullMessage_whenCreatingFailure_thenIllegalArgumentException() {
            assertThatThrownBy(() -> new LlmResponse.Failure(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Error message must not be blank");
        }
    }
}
