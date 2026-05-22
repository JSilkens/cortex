package be.jsilkens.cortex.usecase;

public class StorageDeleteException extends RuntimeException {

    public StorageDeleteException(String path, Throwable cause) {
        super("Failed to delete recording: " + path, cause);
    }
}
