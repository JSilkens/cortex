package be.jsilkens.cortex.storage;

import be.jsilkens.cortex.usecase.StorageDeleteException;
import be.jsilkens.cortex.usecase.StorageInitializationException;
import be.jsilkens.cortex.usecase.StoragePort;
import be.jsilkens.cortex.usecase.StorageReadException;
import be.jsilkens.cortex.usecase.StorageWriteException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class FileStorageService implements StoragePort {

    private final Path baseDirectory;
    private final RecordingIdentifierGenerator identifierGenerator;

    public FileStorageService(
            @Value("${cortex.storage.base-directory:data/recordings}") String baseDirectory) {
        this.baseDirectory = Path.of(baseDirectory);
        this.identifierGenerator = new RecordingIdentifierGenerator();
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        try {
            Files.createDirectories(baseDirectory);
        } catch (IOException e) {
            throw new StorageInitializationException(baseDirectory.toString(), e);
        }
    }

    @Override
    public String save(byte[] data, String filename) {
        var recordingId = identifierGenerator.generate(filename);
        var filePath = baseDirectory.resolve(recordingId);
        try {
            Files.createDirectories(baseDirectory);
            Files.write(filePath, data);
        } catch (IOException e) {
            throw new StorageWriteException(filePath.toString(), e);
        }
        return recordingId;
    }

    @Override
    public byte[] read(String recordingId) {
        var filePath = baseDirectory.resolve(recordingId);
        if (!Files.exists(filePath)) {
            throw new StorageReadException(recordingId, null);
        }
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new StorageReadException(filePath.toString(), e);
        }
    }

    @Override
    public void delete(String recordingId) {
        var filePath = baseDirectory.resolve(recordingId);
        if (!Files.exists(filePath)) {
            throw new StorageDeleteException(recordingId, null);
        }
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new StorageDeleteException(filePath.toString(), e);
        }
    }

    @Override
    public List<String> list() {
        if (!Files.exists(baseDirectory)) {
            return List.of();
        }
        try (var stream = Files.list(baseDirectory)) {
            return stream
                    .map(path -> path.getFileName().toString())
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }
}
