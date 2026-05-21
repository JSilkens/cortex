package be.jsilkens.cortex.domain;

import java.time.Instant;
import java.util.UUID;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.common.domain.validation.Validatable;
import be.jsilkens.cortex.common.domain.validation.Validator;
import be.jsilkens.cortex.domain.rules.MeetingStatusRule;
import be.jsilkens.cortex.domain.rules.MeetingTitleRule;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Meeting implements Validatable {

    private final UUID id;
    private final String title;
    private final String description;
    private final Instant scheduledAt;
    private final MeetingStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Override
    public Outcome<Meeting> validate() {
        return new Validator<Meeting>()
            .addRule(new MeetingTitleRule())
            .addRule(new MeetingStatusRule())
            .validate(this);
    }
}
