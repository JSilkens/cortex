package be.jsilkens.cortex.domain.rules;

import java.util.List;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.Task;

public class TaskPriorityRule implements ValidationRule<Task> {

    @Override
    public void validate(Task task, List<String> errors) {
        if (task.getPriority() == null) {
            errors.add("Task priority must not be null");
        }
    }
}
