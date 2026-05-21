package be.jsilkens.cortex.db.repository;

import java.util.Optional;
import java.util.UUID;

import be.jsilkens.cortex.db.mapper.SettingsEntityMapper;
import be.jsilkens.cortex.domain.Settings;
import be.jsilkens.cortex.domain.repository.SettingsRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SettingsPersistenceFacade implements SettingsRepository {

    private final SettingsJpaRepository repository;

    @Override
    public Settings save(Settings settings) {
        var entity = SettingsEntityMapper.map(settings);
        var saved = repository.save(entity);
        return SettingsEntityMapper.map(saved);
    }

    @Override
    public Optional<Settings> findById(UUID id) {
        return repository.findById(id).map(SettingsEntityMapper::map);
    }

    @Override
    public Optional<Settings> findDefault() {
        return repository.findByIsDefaultTrue().map(SettingsEntityMapper::map);
    }
}
