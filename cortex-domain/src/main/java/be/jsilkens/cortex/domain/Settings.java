package be.jsilkens.cortex.domain;

import java.time.Instant;
import java.util.UUID;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.common.domain.validation.Validatable;
import be.jsilkens.cortex.common.domain.validation.Validator;
import be.jsilkens.cortex.domain.rules.SettingsLlmModelRule;
import be.jsilkens.cortex.domain.rules.SettingsMaxTokensRule;
import be.jsilkens.cortex.domain.rules.SettingsTemperatureRule;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Settings implements Validatable {

    private final UUID id;
    private final String llmModel;
    private final double temperature;
    private final int maxTokens;
    private final boolean isDefault;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Override
    public Outcome<Settings> validate() {
        return new Validator<Settings>()
            .addRule(new SettingsLlmModelRule())
            .addRule(new SettingsTemperatureRule())
            .addRule(new SettingsMaxTokensRule())
            .validate(this);
    }
}
