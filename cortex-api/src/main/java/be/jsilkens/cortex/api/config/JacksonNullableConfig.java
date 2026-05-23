package be.jsilkens.cortex.api.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the JsonNullableModule with Jackson so that generated models
 * using JsonNullable fields serialize and deserialize correctly.
 */
@Configuration
public class JacksonNullableConfig {

    @Bean
    public JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }
}
