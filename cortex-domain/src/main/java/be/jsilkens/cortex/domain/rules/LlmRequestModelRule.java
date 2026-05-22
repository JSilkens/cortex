package be.jsilkens.cortex.domain.rules;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.LlmRequest;
import java.util.List;

public class LlmRequestModelRule implements ValidationRule<LlmRequest> {

    @Override
    public void validate(LlmRequest request, List<String> errors) {
        if (request.getModel() == null || request.getModel().isBlank()) {
            errors.add("Model must not be blank");
        }
    }
}
