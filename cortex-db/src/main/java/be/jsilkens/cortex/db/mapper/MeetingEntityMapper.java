package be.jsilkens.cortex.db.mapper;

import be.jsilkens.cortex.db.entity.MeetingEntity;
import be.jsilkens.cortex.domain.Meeting;
import be.jsilkens.cortex.domain.MeetingStatus;

public final class MeetingEntityMapper {

    private MeetingEntityMapper() {}

    public static Meeting map(MeetingEntity entity) {
        return Meeting.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .scheduledAt(entity.getScheduledAt())
            .status(MeetingStatus.valueOf(entity.getStatus()))
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public static MeetingEntity map(Meeting meeting) {
        return MeetingEntity.builder()
            .id(meeting.getId())
            .title(meeting.getTitle())
            .description(meeting.getDescription())
            .scheduledAt(meeting.getScheduledAt())
            .status(meeting.getStatus().name())
            .build();
    }
}
