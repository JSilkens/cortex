package be.jsilkens.cortex.db.repository;

import java.util.List;
import java.util.UUID;

import be.jsilkens.cortex.db.mapper.LogEntryEntityMapper;
import be.jsilkens.cortex.domain.LogEntry;
import be.jsilkens.cortex.domain.repository.LogEntryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LogEntryPersistenceFacade implements LogEntryRepository {

    private final LogEntryJpaRepository repository;

    @Override
    public LogEntry save(LogEntry logEntry) {
        var entity = LogEntryEntityMapper.map(logEntry);
        var saved = repository.save(entity);
        return LogEntryEntityMapper.map(saved);
    }

    @Override
    public List<LogEntry> findByMeetingId(UUID meetingId) {
        return repository.findByMeetingId(meetingId).stream()
            .map(LogEntryEntityMapper::map)
            .toList();
    }

    @Override
    public List<LogEntry> findAll() {
        return repository.findAll().stream()
            .map(LogEntryEntityMapper::map)
            .toList();
    }
}
