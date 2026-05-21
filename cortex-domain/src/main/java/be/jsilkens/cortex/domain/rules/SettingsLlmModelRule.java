package be.jsilkens.cortex.domain.rules;

import java.util.List;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.Settings;

public class SettingsLlmModelRule implements ValidationRule<Settings> {

    @Override
    public void validate(Settings settings, List<String> errors) {
        if (settings.getLlmModel() == null || settings.getLlmModel().isBlank()) {
            errors.add("LLM model must not be blank");
        }
    }
}
