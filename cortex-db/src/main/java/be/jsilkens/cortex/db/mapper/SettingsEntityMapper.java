package be.jsilkens.cortex.db.mapper;

import be.jsilkens.cortex.db.entity.SettingsEntity;
import be.jsilkens.cortex.domain.Settings;

public final class SettingsEntityMapper {

    private SettingsEntityMapper() {}

    public static Settings map(SettingsEntity entity) {
        return Settings.builder()
            .id(entity.getId())
            .llmModel(entity.getLlmModel())
            .temperature(entity.getTemperature())
            .maxTokens(entity.getMaxTokens())
            .isDefault(entity.isDefault())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public static SettingsEntity map(Settings settings) {
        return SettingsEntity.builder()
            .id(settings.getId())
            .llmModel(settings.getLlmModel())
            .temperature(settings.getTemperature())
            .maxTokens(settings.getMaxTokens())
            .isDefault(settings.isDefault())
            .build();
    }
}
