package be.jsilkens.cortex.domain.rules;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.LlmRequest;
import java.util.List;

public class LlmRequestPromptRule implements ValidationRule<LlmRequest> {

    @Override
    public void validate(LlmRequest request, List<String> errors) {
        if (request.getPrompt() == null || request.getPrompt().isBlank()) {
            errors.add("Prompt must not be blank");
        }
    }
}
