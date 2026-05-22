package be.jsilkens.cortex.usecase;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.domain.LlmRequest;
import be.jsilkens.cortex.domain.LlmResponse;
import be.jsilkens.cortex.domain.repository.LlmPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenerateTextUseCase {

    private static final String DEFAULT_MODEL = "gemma4:26b";
    private static final double DEFAULT_TEMPERATURE = 0.2;
    private static final int DEFAULT_MAX_TOKENS = 4096;

    private final LlmPort llmPort;

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
            return new Outcome.Success<>(response);
        }

        return outcome.map(ignored -> null);
    }
}
