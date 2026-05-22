package be.jsilkens.cortex.llm;

import be.jsilkens.cortex.domain.repository.LlmPort;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmAdapterConfiguration {

    @Bean
    @ConditionalOnProperty(name = "cortex.llm.backend", havingValue = "ollama")
    public LlmPort springAiLlmAdapter(ChatModel chatModel) {
        return new SpringAiLlmAdapter(chatModel);
    }

    @Bean
    @ConditionalOnProperty(
            name = "cortex.llm.backend",
            havingValue = "mock",
            matchIfMissing = true
    )
    public LlmPort mockLlmAdapter() {
        return new MockLlmAdapter();
    }
}
