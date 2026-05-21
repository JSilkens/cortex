package be.jsilkens.cortex.domain.repository;

import java.util.List;
import java.util.UUID;

import be.jsilkens.cortex.domain.LogEntry;

public interface LogEntryRepository {

    LogEntry save(LogEntry logEntry);

    List<LogEntry> findByMeetingId(UUID meetingId);

    List<LogEntry> findAll();
}
