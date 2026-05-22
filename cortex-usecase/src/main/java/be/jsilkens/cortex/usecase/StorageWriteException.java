package be.jsilkens.cortex.usecase;

public class StorageWriteException extends RuntimeException {

    public StorageWriteException(String path, Throwable cause) {
        super("Failed to write recording: " + path, cause);
    }
}
