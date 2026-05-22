package be.jsilkens.cortex.domain.repository;

import be.jsilkens.cortex.domain.LlmRequest;
import be.jsilkens.cortex.domain.LlmResponse;

public interface LlmPort {

    LlmResponse generate(LlmRequest request);
}
