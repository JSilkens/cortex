package be.jsilkens.cortex.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class RecordingIdentifierGenerator {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssSSS");

    String generate(String originalFilename) {
        var timestamp = LocalDateTime.now().format(FORMATTER);
        var sanitized = sanitizeFilename(originalFilename);
        return timestamp + "_" + sanitized;
    }

    String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
