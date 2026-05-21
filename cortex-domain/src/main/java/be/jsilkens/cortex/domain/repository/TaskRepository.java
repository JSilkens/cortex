package be.jsilkens.cortex.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import be.jsilkens.cortex.domain.Task;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(UUID id);

    List<Task> findByMeetingId(UUID meetingId);

    List<Task> findAll();

    void deleteById(UUID id);
}
