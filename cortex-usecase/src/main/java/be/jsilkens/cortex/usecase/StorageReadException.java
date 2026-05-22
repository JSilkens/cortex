package be.jsilkens.cortex.usecase;

public class StorageReadException extends RuntimeException {

    public StorageReadException(String path, Throwable cause) {
        super("Failed to read recording: " + path, cause);
    }
}
