package be.jsilkens.cortex.llm;

import be.jsilkens.cortex.domain.LlmResponse;
import be.jsilkens.cortex.domain.testdata.LlmRequestTestdata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SpringAiLlmAdapter")
@ExtendWith(MockitoExtension.class)
class SpringAiLlmAdapterTest {

    @Mock
    private ChatModel chatModel;

    @InjectMocks
    private SpringAiLlmAdapter adapter;

    @DisplayName("GIVEN valid request and successful ChatModel WHEN generating THEN returns Success with response text")
    @Test
    void givenValidRequestAndSuccessfulChatModel_whenGenerating_thenReturnsSuccessWithResponseText() {
        var request = LlmRequestTestdata.fullTestData();
        var chatResponse = mock(ChatResponse.class);
        var generation = mock(Generation.class);
        var assistantMessage = new AssistantMessage("Generated text");
        when(generation.getOutput()).thenReturn(assistantMessage);
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        var actual = adapter.generate(request);

        assertThat(actual).isInstanceOf(LlmResponse.Success.class);
        assertThat(((LlmResponse.Success) actual).text()).isEqualTo("Generated text");
    }

    @DisplayName("GIVEN valid request and ChatModel throws SocketTimeoutException WHEN generating THEN returns Failure with timeout message")
    @Test
    void givenChatModelThrowsSocketTimeoutException_whenGenerating_thenReturnsFailureWithTimeoutMessage() {
        var request = LlmRequestTestdata.fullTestData();
        when(chatModel.call(any(Prompt.class))).thenAnswer(invocation -> {
            throw new SocketTimeoutException("Read timed out");
        });

        var actual = adapter.generate(request);

        assertThat(actual).isInstanceOf(LlmResponse.Failure.class);
        assertThat(((LlmResponse.Failure) actual).errorMessage()).startsWith("LLM request timed out:");
        assertThat(((LlmResponse.Failure) actual).errorMessage()).contains("Read timed out");
    }

    @DisplayName("GIVEN valid request and ChatModel throws ConnectException WHEN generating THEN returns Failure with connectivity message")
    @Test
    void givenChatModelThrowsConnectException_whenGenerating_thenReturnsFailureWithConnectivityMessage() {
        var request = LlmRequestTestdata.fullTestData();
        when(chatModel.call(any(Prompt.class))).thenAnswer(invocation -> {
            throw new ConnectException("Connection refused");
        });

        var actual = adapter.generate(request);

        assertThat(actual).isInstanceOf(LlmResponse.Failure.class);
        assertThat(((LlmResponse.Failure) actual).errorMessage()).startsWith("LLM connection failed:");
        assertThat(((LlmResponse.Failure) actual).errorMessage()).contains("Connection refused");
    }

    @DisplayName("GIVEN valid request and ChatModel throws RuntimeException WHEN generating THEN returns Failure with generic error message")
    @Test
    void givenChatModelThrowsRuntimeException_whenGenerating_thenReturnsFailureWithGenericErrorMessage() {
        var request = LlmRequestTestdata.fullTestData();
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("Something went wrong"));

        var actual = adapter.generate(request);

        assertThat(actual).isInstanceOf(LlmResponse.Failure.class);
        assertThat(((LlmResponse.Failure) actual).errorMessage()).contains("LLM generation error");
    }

    @DisplayName("GIVEN valid request WHEN generating THEN ChatModel receives Prompt with correct model, temperature, maxTokens")
    @Test
    void givenValidRequest_whenGenerating_thenChatModelReceivesPromptWithCorrectOptions() {
        var request = LlmRequestTestdata.validBuilder()
                .prompt("Test prompt")
                .model("gemma4:26b")
                .temperature(0.5)
                .maxTokens(2048)
                .build();
        var chatResponse = mock(ChatResponse.class);
        var generation = mock(Generation.class);
        var assistantMessage = new AssistantMessage("Response");
        when(generation.getOutput()).thenReturn(assistantMessage);
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        adapter.generate(request);

        var promptCaptor = ArgumentCaptor.forClass(Prompt.class);
        verify(chatModel).call(promptCaptor.capture());
        var capturedPrompt = promptCaptor.getValue();

        assertThat(capturedPrompt.getContents()).isEqualTo("Test prompt");
        assertThat(capturedPrompt.getOptions()).isInstanceOf(OllamaChatOptions.class);
        var options = (OllamaChatOptions) capturedPrompt.getOptions();
        assertThat(options.getModel()).isEqualTo("gemma4:26b");
        assertThat(options.getTemperature()).isEqualTo(0.5);
        assertThat(options.getMaxTokens()).isEqualTo(2048);
    }
}
