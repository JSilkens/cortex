package be.jsilkens.cortex.domain.rules;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.LlmRequest;
import java.util.List;

public class LlmRequestMaxTokensRule implements ValidationRule<LlmRequest> {

    @Override
    public void validate(LlmRequest request, List<String> errors) {
        if (request.getMaxTokens() <= 0) {
            errors.add("Max tokens must be positive");
        }
    }
}
