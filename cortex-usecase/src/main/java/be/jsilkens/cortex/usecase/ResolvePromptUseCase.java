package be.jsilkens.cortex.usecase;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.domain.repository.PromptTemplatePort;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class ResolvePromptUseCase {

    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9\\-]+$");

    private final PromptTemplatePort promptTemplatePort;

    public Outcome<String> execute(String templateName, Map<String, Object> variables) {
        var effectiveVariables = variables != null ? variables : Map.<String, Object>of();

        var validationResult = validateTemplateName(templateName);
        if (validationResult instanceof Outcome.Failure<String>) {
            return validationResult;
        }

        if (!promptTemplatePort.templateExists(templateName)) {
            return new Outcome.Failure<>("Template not found: " + templateName);
        }

        return promptTemplatePort.resolve(templateName, effectiveVariables);
    }

    private Outcome<String> validateTemplateName(String templateName) {
        if (templateName == null || templateName.isBlank()) {
            return new Outcome.Failure<>("Template name must not be blank");
        }
        if (templateName.contains("..") && (templateName.contains("/") || templateName.contains("\\"))) {
            return new Outcome.Failure<>("Template name must not contain path traversal sequences");
        }
        if (!VALID_NAME_PATTERN.matcher(templateName).matches()) {
            return new Outcome.Failure<>("Template name must contain only lowercase letters, digits, and hyphens");
        }
        return new Outcome.Success<>(templateName);
    }
}
