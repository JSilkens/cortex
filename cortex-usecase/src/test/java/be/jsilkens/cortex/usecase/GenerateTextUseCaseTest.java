package be.jsilkens.cortex.usecase;

import be.jsilkens.cortex.common.domain.event.DomainEventPublisher;
import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.domain.LlmRequest;
import be.jsilkens.cortex.domain.LlmResponse;
import be.jsilkens.cortex.domain.repository.LlmPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateTextUseCaseTest {

    @Mock
    private LlmPort llmPort;

    @Mock
    private DomainEventPublisher eventPublisher;

    private GenerateTextUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GenerateTextUseCase(llmPort, eventPublisher);
    }

    @DisplayName("GIVEN valid prompt WHEN executing with defaults THEN LlmPort receives request with default model/temperature/maxTokens")
    @Test
    void givenValidPrompt_whenExecutingWithDefaults_thenLlmPortReceivesRequestWithDefaults() {
        var expectedResponse = new LlmResponse.Success("Generated text");
        when(llmPort.generate(any(LlmRequest.class))).thenReturn(expectedResponse);

        Outcome<LlmResponse> result = useCase.execute("Summarize this meeting");

        assertThat(result).isInstanceOf(Outcome.Success.class);

        var captor = ArgumentCaptor.forClass(LlmRequest.class);
        verify(llmPort).generate(captor.capture());

        var capturedRequest = captor.getValue();
        assertThat(capturedRequest.getPrompt()).isEqualTo("Summarize this meeting");
        assertThat(capturedRequest.getModel()).isEqualTo("gemma4:26b");
        assertThat(capturedRequest.getTemperature()).isEqualTo(0.2);
        assertThat(capturedRequest.getMaxTokens()).isEqualTo(4096);
    }

    @DisplayName("GIVEN valid prompt and model WHEN executing THEN LlmPort receives request with specified model")
    @Test
    void givenValidPromptAndModel_whenExecuting_thenLlmPortReceivesRequestWithSpecifiedModel() {
        var expectedResponse = new LlmResponse.Success("Generated text");
        when(llmPort.generate(any(LlmRequest.class))).thenReturn(expectedResponse);

        Outcome<LlmResponse> result = useCase.execute("Summarize this meeting", "gemma4:e4b");

        assertThat(result).isInstanceOf(Outcome.Success.class);

        var captor = ArgumentCaptor.forClass(LlmRequest.class);
        verify(llmPort).generate(captor.capture());

        var capturedRequest = captor.getValue();
        assertThat(capturedRequest.getPrompt()).isEqualTo("Summarize this meeting");
        assertThat(capturedRequest.getModel()).isEqualTo("gemma4:e4b");
        assertThat(capturedRequest.getTemperature()).isEqualTo(0.2);
        assertThat(capturedRequest.getMaxTokens()).isEqualTo(4096);
    }

    @DisplayName("GIVEN valid LlmRequest WHEN executing THEN validates first, calls LlmPort, returns Outcome.Success wrapping LlmResponse")
    @Test
    void givenValidLlmRequest_whenExecuting_thenValidatesAndCallsLlmPortAndReturnsSuccess() {
        var request = LlmRequest.builder()
                .prompt("Summarize this meeting")
                .model("gemma4:26b")
                .temperature(0.5)
                .maxTokens(2048)
                .build();
        var expectedResponse = new LlmResponse.Success("Generated text");
        when(llmPort.generate(request)).thenReturn(expectedResponse);

        Outcome<LlmResponse> result = useCase.execute(request);

        assertThat(result).isInstanceOf(Outcome.Success.class);
        assertThat(((Outcome.Success<LlmResponse>) result).value()).isEqualTo(expectedResponse);
        verify(llmPort).generate(request);
    }

    @DisplayName("GIVEN LlmPort returns LlmResponse.Failure WHEN executing THEN use case still returns Outcome.Success")
    @Test
    void givenLlmPortReturnsFailure_whenExecuting_thenUseCaseReturnsOutcomeSuccess() {
        var request = LlmRequest.builder()
                .prompt("Summarize this meeting")
                .model("gemma4:26b")
                .temperature(0.2)
                .maxTokens(4096)
                .build();
        var failureResponse = new LlmResponse.Failure("LLM connection failed");
        when(llmPort.generate(request)).thenReturn(failureResponse);

        Outcome<LlmResponse> result = useCase.execute(request);

        assertThat(result).isInstanceOf(Outcome.Success.class);
        assertThat(((Outcome.Success<LlmResponse>) result).value()).isEqualTo(failureResponse);
    }

    @DisplayName("GIVEN invalid LlmRequest with blank prompt WHEN executing THEN returns Outcome.Failure without calling LlmPort")
    @Test
    void givenInvalidLlmRequestBlankPrompt_whenExecuting_thenReturnsOutcomeFailureWithoutCallingLlmPort() {
        var request = LlmRequest.builder()
                .prompt("")
                .model("gemma4:26b")
                .temperature(0.2)
                .maxTokens(4096)
                .build();

        Outcome<LlmResponse> result = useCase.execute(request);

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        assertThat(((Outcome.Failure<LlmResponse>) result).errors())
                .contains("Prompt must not be blank");
        verify(llmPort, never()).generate(any());
    }

    @DisplayName("GIVEN LlmRequest with multiple invalid fields WHEN executing THEN returns Outcome.Failure with all error messages")
    @Test
    void givenLlmRequestWithMultipleInvalidFields_whenExecuting_thenReturnsOutcomeFailureWithAllErrors() {
        var request = LlmRequest.builder()
                .prompt("")
                .model("")
                .temperature(3.0)
                .maxTokens(-1)
                .build();

        Outcome<LlmResponse> result = useCase.execute(request);

        assertThat(result).isInstanceOf(Outcome.Failure.class);
        var errors = ((Outcome.Failure<LlmResponse>) result).errors();
        assertThat(errors)
                .contains("Prompt must not be blank")
                .contains("Model must not be blank")
                .contains("Temperature must be between 0.0 and 2.0 inclusive")
                .contains("Max tokens must be positive");
        verify(llmPort, never()).generate(any());
    }
}
