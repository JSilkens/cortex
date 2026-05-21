package be.jsilkens.cortex.domain.repository;

import java.util.Optional;
import java.util.UUID;

import be.jsilkens.cortex.domain.Settings;

public interface SettingsRepository {

    Settings save(Settings settings);

    Optional<Settings> findById(UUID id);

    Optional<Settings> findDefault();
}
