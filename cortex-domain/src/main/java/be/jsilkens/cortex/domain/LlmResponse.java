package be.jsilkens.cortex.domain;

public sealed interface LlmResponse permits LlmResponse.Success, LlmResponse.Failure {

    record Success(String text) implements LlmResponse {
        public Success {
            if (text == null) {
                throw new IllegalArgumentException("Response text must not be null");
            }
        }
    }

    record Failure(String errorMessage) implements LlmResponse {
        public Failure {
            if (errorMessage == null || errorMessage.isBlank()) {
                throw new IllegalArgumentException("Error message must not be blank");
            }
        }
    }
}
