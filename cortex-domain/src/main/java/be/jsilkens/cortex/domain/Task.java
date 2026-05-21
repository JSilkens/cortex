package be.jsilkens.cortex.domain;

import java.time.Instant;
import java.util.UUID;

import be.jsilkens.cortex.common.domain.validation.Outcome;
import be.jsilkens.cortex.common.domain.validation.Validatable;
import be.jsilkens.cortex.common.domain.validation.Validator;
import be.jsilkens.cortex.domain.rules.TaskPriorityRule;
import be.jsilkens.cortex.domain.rules.TaskStatusRule;
import be.jsilkens.cortex.domain.rules.TaskTitleRule;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Task implements Validatable {

    private final UUID id;
    private final UUID meetingId;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final TaskPriority priority;
    private final Instant dueAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Override
    public Outcome<Task> validate() {
        return new Validator<Task>()
            .addRule(new TaskTitleRule())
            .addRule(new TaskStatusRule())
            .addRule(new TaskPriorityRule())
            .validate(this);
    }
}
