package be.jsilkens.cortex.usecase;

import java.util.List;

public interface StoragePort {

    String save(byte[] data, String filename);

    byte[] read(String recordingId);

    void delete(String recordingId);

    List<String> list();
}
