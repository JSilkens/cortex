package be.jsilkens.cortex.usecase;

import be.jsilkens.cortex.common.domain.event.DomainEvent;
import be.jsilkens.cortex.common.domain.event.DomainEventPublisher;
import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.domain.LlmRequest;
import be.jsilkens.cortex.domain.LlmResponse;
import be.jsilkens.cortex.domain.event.LlmGenerationFailedEvent;
import be.jsilkens.cortex.domain.event.TextGeneratedEvent;
import be.jsilkens.cortex.domain.repository.LlmPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenerateTextUseCase {

    private static final String DEFAULT_MODEL = "gemma4:26b";
    private static final double DEFAULT_TEMPERATURE = 0.2;
    private static final int DEFAULT_MAX_TOKENS = 4096;

    private final LlmPort llmPort;
    private final DomainEventPublisher eventPublisher;

    public Outcome<LlmResponse> execute(String prompt) {
        var request = LlmRequest.builder()
                .prompt(prompt)
                .model(DEFAULT_MODEL)
                .temperature(DEFAULT_TEMPERATURE)
                .maxTokens(DEFAULT_MAX_TOKENS)
                .build();
        return execute(request);
    }

    public Outcome<LlmResponse> execute(String prompt, String model) {
        var request = LlmRequest.builder()
                .prompt(prompt)
                .model(model)
                .temperature(DEFAULT_TEMPERATURE)
                .maxTokens(DEFAULT_MAX_TOKENS)
                .build();
        return execute(request);
    }

    public Outcome<LlmResponse> execute(LlmRequest request) {
        Outcome<LlmRequest> outcome = request.validate();

        if (outcome instanceof Outcome.Success<LlmRequest>) {
            var response = llmPort.generate(request);
            publishEvent(request, response);
            return new Outcome.Success<>(response);
        }

        return outcome.map(ignored -> null);
    }

    private void publishEvent(LlmRequest request, LlmResponse response) {
        try {
            DomainEvent event = switch (response) {
                case LlmResponse.Success s -> new TextGeneratedEvent(
                        request.getPrompt(), request.getModel(), s.text());
                case LlmResponse.Failure f -> new LlmGenerationFailedEvent(
                        request.getPrompt(), request.getModel(), f.errorMessage());
            };
            eventPublisher.publish(event);
        } catch (Exception e) {
            // Swallow — event publishing must never affect the main flow
        }
    }
}
