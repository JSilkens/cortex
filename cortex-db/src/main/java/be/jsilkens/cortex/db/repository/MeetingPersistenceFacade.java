package be.jsilkens.cortex.db.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import be.jsilkens.cortex.db.mapper.MeetingEntityMapper;
import be.jsilkens.cortex.domain.Meeting;
import be.jsilkens.cortex.domain.repository.MeetingRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeetingPersistenceFacade implements MeetingRepository {

    private final MeetingJpaRepository repository;

    @Override
    public Meeting save(Meeting meeting) {
        var entity = MeetingEntityMapper.map(meeting);
        var saved = repository.save(entity);
        return MeetingEntityMapper.map(saved);
    }

    @Override
    public Optional<Meeting> findById(UUID id) {
        return repository.findById(id).map(MeetingEntityMapper::map);
    }

    @Override
    public List<Meeting> findAll() {
        return repository.findAll().stream()
            .map(MeetingEntityMapper::map)
            .toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
