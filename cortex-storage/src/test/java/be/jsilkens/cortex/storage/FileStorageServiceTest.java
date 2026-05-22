package be.jsilkens.cortex.storage;

import be.jsilkens.cortex.usecase.StorageDeleteException;
import be.jsilkens.cortex.usecase.StorageReadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService service;

    @BeforeEach
    void setUp() {
        service = new FileStorageService(tempDir.toString());
    }

    @DisplayName("GIVEN valid byte array and filename WHEN save THEN file is created in directory")
    @Test
    void givenValidData_whenSave_thenFileCreatedInDirectory() {
        var data = "audio content".getBytes();
        var filename = "meeting.wav";

        var recordingId = service.save(data, filename);

        var savedFile = tempDir.resolve(recordingId);
        assertThat(savedFile).exists();
        assertThat(savedFile).isRegularFile();
    }

    @DisplayName("GIVEN base directory does not exist WHEN save THEN directory is created and file is written")
    @Test
    void givenMissingDirectory_whenSave_thenDirectoryCreatedAndFileWritten() {
        var missingDir = tempDir.resolve("nested/subdir");
        var isolatedService = new FileStorageService(missingDir.toString());
        var data = "audio content".getBytes();
        var filename = "recording.wav";

        var recordingId = isolatedService.save(data, filename);

        assertThat(missingDir).exists();
        assertThat(missingDir.resolve(recordingId)).exists();
    }

    @DisplayName("GIVEN existing recording WHEN read THEN correct bytes are returned")
    @Test
    void givenExistingRecording_whenRead_thenCorrectBytesReturned() {
        var data = "expected audio bytes".getBytes();
        var filename = "playback.wav";

        var recordingId = service.save(data, filename);
        var result = service.read(recordingId);

        assertThat(result).isEqualTo(data);
    }

    @DisplayName("GIVEN non-existent recording ID WHEN read THEN StorageReadException is thrown")
    @Test
    void givenNonExistentId_whenRead_thenStorageReadExceptionThrown() {
        var nonExistentId = "20250101T120000000_ghost.wav";

        assertThatThrownBy(() -> service.read(nonExistentId))
                .isInstanceOf(StorageReadException.class);
    }

    @DisplayName("GIVEN existing recording WHEN delete THEN file is removed from disk")
    @Test
    void givenExistingRecording_whenDelete_thenFileRemovedFromDisk() {
        var data = "to be deleted".getBytes();
        var filename = "temporary.wav";
        var recordingId = service.save(data, filename);

        service.delete(recordingId);

        var deletedFile = tempDir.resolve(recordingId);
        assertThat(deletedFile).doesNotExist();
    }

    @DisplayName("GIVEN non-existent recording ID WHEN delete THEN StorageDeleteException is thrown")
    @Test
    void givenNonExistentId_whenDelete_thenStorageDeleteExceptionThrown() {
        var nonExistentId = "20250101T120000000_missing.wav";

        assertThatThrownBy(() -> service.delete(nonExistentId))
                .isInstanceOf(StorageDeleteException.class);
    }

    @DisplayName("GIVEN empty directory WHEN list THEN empty list is returned")
    @Test
    void givenEmptyDirectory_whenList_thenEmptyListReturned() {
        var result = service.list();

        assertThat(result).isEmpty();
    }

    @DisplayName("GIVEN directory does not exist WHEN list THEN empty list is returned")
    @Test
    void givenDirectoryDoesNotExist_whenList_thenEmptyListReturned() throws IOException {
        var ephemeralDir = tempDir.resolve("ephemeral");
        var ephemeralService = new FileStorageService(ephemeralDir.toString());
        Files.delete(ephemeralDir);

        var result = ephemeralService.list();

        assertThat(result).isEmpty();
    }

    @DisplayName("GIVEN base directory path WHEN service is initialized THEN directory is created")
    @Test
    void givenBasePath_whenInitialized_thenDirectoryIsCreated() {
        var newDir = tempDir.resolve("auto-created");

        new FileStorageService(newDir.toString());

        assertThat(newDir).exists();
        assertThat(newDir).isDirectory();
    }
}
