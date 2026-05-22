package be.jsilkens.cortex.llm;

import be.jsilkens.cortex.domain.LlmRequest;
import be.jsilkens.cortex.domain.LlmResponse;
import be.jsilkens.cortex.domain.repository.LlmPort;

/**
 * Development adapter that echoes the prompt without network calls.
 */
public class MockLlmAdapter implements LlmPort {

    @Override
    public LlmResponse generate(LlmRequest request) {
        var echoText = "[mock:%s] %s".formatted(request.getModel(), request.getPrompt());
        return new LlmResponse.Success(echoText);
    }
}
