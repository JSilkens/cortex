package be.jsilkens.cortex.domain.rules;

import java.util.List;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.Settings;

public class SettingsTemperatureRule implements ValidationRule<Settings> {

    @Override
    public void validate(Settings settings, List<String> errors) {
        if (settings.getTemperature() < 0.0 || settings.getTemperature() > 2.0) {
            errors.add("Temperature must be between 0.0 and 2.0");
        }
    }
}
