package be.jsilkens.cortex.db.mapper;

import be.jsilkens.cortex.db.entity.TaskEntity;
import be.jsilkens.cortex.domain.Task;
import be.jsilkens.cortex.domain.TaskPriority;
import be.jsilkens.cortex.domain.TaskStatus;

public final class TaskEntityMapper {

    private TaskEntityMapper() {}

    public static Task map(TaskEntity entity) {
        return Task.builder()
            .id(entity.getId())
            .meetingId(entity.getMeetingId())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .status(TaskStatus.valueOf(entity.getStatus()))
            .priority(TaskPriority.valueOf(entity.getPriority()))
            .dueAt(entity.getDueAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public static TaskEntity map(Task task) {
        return TaskEntity.builder()
            .id(task.getId())
            .meetingId(task.getMeetingId())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus().name())
            .priority(task.getPriority().name())
            .dueAt(task.getDueAt())
            .build();
    }
}
