package be.jsilkens.cortex.domain;

import java.time.Instant;
import java.util.UUID;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.common.domain.validation.Validatable;
import be.jsilkens.cortex.common.domain.validation.Validator;
import be.jsilkens.cortex.domain.rules.LogEntryContentRule;
import be.jsilkens.cortex.domain.rules.LogEntryTypeRule;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LogEntry implements Validatable {

    private final UUID id;
    private final UUID meetingId;
    private final String content;
    private final LogEntryType type;
    private final Instant createdAt;

    @Override
    public Outcome<LogEntry> validate() {
        return new Validator<LogEntry>()
            .addRule(new LogEntryContentRule())
            .addRule(new LogEntryTypeRule())
            .validate(this);
    }
}
