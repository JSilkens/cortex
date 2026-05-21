package be.jsilkens.cortex.db.repository;

import java.util.List;
import java.util.UUID;

import be.jsilkens.cortex.db.entity.TaskEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findByMeetingId(UUID meetingId);
}
