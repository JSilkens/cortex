package be.jsilkens.cortex.db.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import be.jsilkens.cortex.db.mapper.TaskEntityMapper;
import be.jsilkens.cortex.domain.Task;
import be.jsilkens.cortex.domain.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskPersistenceFacade implements TaskRepository {

    private final TaskJpaRepository repository;

    @Override
    public Task save(Task task) {
        var entity = TaskEntityMapper.map(task);
        var saved = repository.save(entity);
        return TaskEntityMapper.map(saved);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return repository.findById(id).map(TaskEntityMapper::map);
    }

    @Override
    public List<Task> findByMeetingId(UUID meetingId) {
        return repository.findByMeetingId(meetingId).stream()
            .map(TaskEntityMapper::map)
            .toList();
    }

    @Override
    public List<Task> findAll() {
        return repository.findAll().stream()
            .map(TaskEntityMapper::map)
            .toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
