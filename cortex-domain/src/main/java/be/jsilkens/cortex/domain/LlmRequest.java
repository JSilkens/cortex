package be.jsilkens.cortex.domain;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.common.domain.validation.Validatable;
import be.jsilkens.cortex.common.domain.validation.Validator;
import be.jsilkens.cortex.domain.rules.LlmRequestMaxTokensRule;
import be.jsilkens.cortex.domain.rules.LlmRequestModelRule;
import be.jsilkens.cortex.domain.rules.LlmRequestPromptRule;
import be.jsilkens.cortex.domain.rules.LlmRequestTemperatureRule;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LlmRequest implements Validatable {

    private final String prompt;
    private final String model;
    private final double temperature;
    private final int maxTokens;

    @Override
    public Outcome<LlmRequest> validate() {
        return new Validator<LlmRequest>()
                .addRule(new LlmRequestPromptRule())
                .addRule(new LlmRequestModelRule())
                .addRule(new LlmRequestTemperatureRule())
                .addRule(new LlmRequestMaxTokensRule())
                .validate(this);
    }
}
