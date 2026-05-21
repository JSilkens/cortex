package be.jsilkens.cortex.db.repository;

import java.util.List;
import java.util.UUID;

import be.jsilkens.cortex.db.entity.LogEntryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEntryJpaRepository extends JpaRepository<LogEntryEntity, UUID> {

    List<LogEntryEntity> findByMeetingId(UUID meetingId);
}
