package be.jsilkens.cortex.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import be.jsilkens.cortex.domain.Meeting;

public interface MeetingRepository {

    Meeting save(Meeting meeting);

    Optional<Meeting> findById(UUID id);

    List<Meeting> findAll();

    void deleteById(UUID id);
}
