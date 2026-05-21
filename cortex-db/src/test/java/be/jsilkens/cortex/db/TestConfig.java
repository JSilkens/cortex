package be.jsilkens.cortex.db;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableAutoConfiguration
@EnableJpaAuditing
@Import(FlywayAutoConfiguration.class)
@ComponentScan("be.jsilkens.cortex.db")
public class TestConfig {
}
