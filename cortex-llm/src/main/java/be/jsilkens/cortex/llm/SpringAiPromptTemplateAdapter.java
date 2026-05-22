package be.jsilkens.cortex.llm;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.domain.repository.PromptTemplatePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpringAiPromptTemplateAdapter implements PromptTemplatePort {

    private static final String TEMPLATE_PATH_PREFIX = "classpath:prompts/";
    private static final String TEMPLATE_EXTENSION = ".txt";

    private final ResourceLoader resourceLoader;
    private final ConcurrentHashMap<String, String> templateCache = new ConcurrentHashMap<>();

    public SpringAiPromptTemplateAdapter(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Outcome<String> resolve(String templateName, Map<String, Object> variables) {
        try {
            var content = loadTemplate(templateName);
            if (content == null) {
                return new Outcome.Failure<>("Template not found: " + templateName);
            }
            var promptTemplate = new PromptTemplate(content);
            var resolved = promptTemplate.render(variables);
            return new Outcome.Success<>(resolved);
        } catch (IllegalArgumentException e) {
            log.warn("Variable substitution failed for template '{}': {}", templateName, e.getMessage());
            return new Outcome.Failure<>("Unresolved placeholder in template '" + templateName + "': " + e.getMessage());
        } catch (Exception e) {
            log.warn("Failed to resolve template '{}': {}", templateName, e.getMessage(), e);
            return new Outcome.Failure<>("Failed to resolve template '" + templateName + "': " + e.getMessage());
        }
    }

    @Override
    public boolean templateExists(String templateName) {
        if (templateCache.containsKey(templateName)) {
            return true;
        }
        var resource = resourceLoader.getResource(buildResourcePath(templateName));
        return resource.exists();
    }

    @Override
    public void reloadTemplates() {
        templateCache.clear();
        log.info("Template cache cleared — next resolution will reload from classpath");
    }

    private String loadTemplate(String templateName) {
        return templateCache.computeIfAbsent(templateName, this::readFromClasspath);
    }

    private String readFromClasspath(String templateName) {
        var resource = resourceLoader.getResource(buildResourcePath(templateName));
        if (!resource.exists()) {
            return null;
        }
        try (var inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to read template file '{}': {}", templateName, e.getMessage(), e);
            return null;
        }
    }

    private String buildResourcePath(String templateName) {
        return TEMPLATE_PATH_PREFIX + templateName + TEMPLATE_EXTENSION;
    }
}
