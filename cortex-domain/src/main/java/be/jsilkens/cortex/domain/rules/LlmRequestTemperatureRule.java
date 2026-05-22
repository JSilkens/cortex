package be.jsilkens.cortex.domain.rules;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.LlmRequest;
import java.util.List;

public class LlmRequestTemperatureRule implements ValidationRule<LlmRequest> {

    @Override
    public void validate(LlmRequest request, List<String> errors) {
        if (request.getTemperature() < 0.0 || request.getTemperature() > 2.0) {
            errors.add("Temperature must be between 0.0 and 2.0 inclusive");
        }
    }
}
