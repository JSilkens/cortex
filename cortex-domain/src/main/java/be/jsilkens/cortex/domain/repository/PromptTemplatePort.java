package be.jsilkens.cortex.domain.repository;

import be.jsilkens.cortex.common.domain.validation.Outcome;

import java.util.Map;

public interface PromptTemplatePort {

    Outcome<String> resolve(String templateName, Map<String, Object> variables);

    boolean templateExists(String templateName);

    void reloadTemplates();
}
