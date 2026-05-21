package be.jsilkens.cortex.db.repository;

import java.util.Optional;
import java.util.UUID;

import be.jsilkens.cortex.db.entity.SettingsEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsJpaRepository extends JpaRepository<SettingsEntity, UUID> {

    Optional<SettingsEntity> findByIsDefaultTrue();
}
