package be.jsilkens.cortex.event;

import be.jsilkens.cortex.domain.LogEntry;
import be.jsilkens.cortex.domain.LogEntryType;
import be.jsilkens.cortex.domain.event.TextGeneratedEvent;
import be.jsilkens.cortex.domain.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LlmLoggingListener {

    private final LogEntryRepository logEntryRepository;

    @EventListener
    public void onTextGenerated(TextGeneratedEvent event) {
        try {
            var logEntry = LogEntry.builder()
                    .id(UUID.randomUUID())
                    .content(event.generatedText())
                    .type(LogEntryType.NOTE)
                    .createdAt(Instant.now())
                    .build();
            logEntryRepository.save(logEntry);
        } catch (Exception e) {
            log.warn("Failed to persist log entry for TextGeneratedEvent", e);
        }
    }
}
