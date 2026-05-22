package be.jsilkens.cortex.llm;

import be.jsilkens.cortex.domain.repository.PromptTemplatePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class PromptTemplateAdapterConfiguration {

    @Bean
    public PromptTemplatePort promptTemplateAdapter(ResourceLoader resourceLoader) {
        return new SpringAiPromptTemplateAdapter(resourceLoader);
    }
}
