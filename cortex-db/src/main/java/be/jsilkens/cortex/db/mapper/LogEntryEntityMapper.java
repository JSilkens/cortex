package be.jsilkens.cortex.db.mapper;

import be.jsilkens.cortex.db.entity.LogEntryEntity;
import be.jsilkens.cortex.domain.LogEntry;
import be.jsilkens.cortex.domain.LogEntryType;

public final class LogEntryEntityMapper {

    private LogEntryEntityMapper() {}

    public static LogEntry map(LogEntryEntity entity) {
        return LogEntry.builder()
            .id(entity.getId())
            .meetingId(entity.getMeetingId())
            .content(entity.getContent())
            .type(LogEntryType.valueOf(entity.getType()))
            .createdAt(entity.getCreatedAt())
            .build();
    }

    public static LogEntryEntity map(LogEntry logEntry) {
        return LogEntryEntity.builder()
            .id(logEntry.getId())
            .meetingId(logEntry.getMeetingId())
            .content(logEntry.getContent())
            .type(logEntry.getType().name())
            .build();
    }
}
