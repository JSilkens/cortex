package be.jsilkens.cortex.db.repository;

import java.util.UUID;

import be.jsilkens.cortex.db.entity.MeetingEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingJpaRepository extends JpaRepository<MeetingEntity, UUID> {
}
