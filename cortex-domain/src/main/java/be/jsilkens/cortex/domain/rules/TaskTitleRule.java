package be.jsilkens.cortex.domain.rules;

import java.util.List;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.Task;

public class TaskTitleRule implements ValidationRule<Task> {

    @Override
    public void validate(Task task, List<String> errors) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            errors.add("Task title must not be blank");
        }
    }
}
