package be.jsilkens.cortex.usecase;

public class StorageInitializationException extends RuntimeException {

    public StorageInitializationException(String path, Throwable cause) {
        super("Failed to initialize storage directory: " + path, cause);
    }
}
