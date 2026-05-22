package be.jsilkens.cortex.llm;

import be.jsilkens.cortex.domain.LlmRequest;
import be.jsilkens.cortex.domain.LlmResponse;
import be.jsilkens.cortex.domain.repository.LlmPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaChatOptions;

@Slf4j
@RequiredArgsConstructor
public class SpringAiLlmAdapter implements LlmPort {

    private final ChatModel chatModel;

    @Override
    public LlmResponse generate(LlmRequest request) {
        try {
            var options = OllamaChatOptions.builder()
                    .model(request.getModel())
                    .temperature((double) request.getTemperature())
                    .maxTokens(request.getMaxTokens())
                    .build();

            var prompt = new Prompt(request.getPrompt(), options);
            ChatResponse response = chatModel.call(prompt);

            var text = response.getResult().getOutput().getText();
            return new LlmResponse.Success(text);
        } catch (Exception e) {
            log.warn("LLM generation failed for model '{}': {}", request.getModel(), e.getMessage(), e);
            return new LlmResponse.Failure(categorizeError(e));
        }
    }

    private String categorizeError(Exception e) {
        var message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();

        if (isTimeoutException(e)) {
            return "LLM request timed out: " + message;
        }
        if (isConnectivityException(e)) {
            return "LLM connection failed: " + message;
        }
        return "LLM generation error: " + message;
    }

    private boolean isTimeoutException(Exception e) {
        return e instanceof java.net.SocketTimeoutException
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("timeout"));
    }

    private boolean isConnectivityException(Exception e) {
        return e instanceof java.net.ConnectException
                || e instanceof java.net.UnknownHostException
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("connection refused"));
    }
}
