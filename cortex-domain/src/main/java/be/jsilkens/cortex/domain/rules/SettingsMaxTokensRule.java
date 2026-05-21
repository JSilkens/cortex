package be.jsilkens.cortex.domain.rules;

import java.util.List;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.Settings;

public class SettingsMaxTokensRule implements ValidationRule<Settings> {

    @Override
    public void validate(Settings settings, List<String> errors) {
        if (settings.getMaxTokens() <= 0) {
            errors.add("Max tokens must be positive");
        }
    }
}
