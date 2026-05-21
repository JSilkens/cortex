package be.jsilkens.cortex.domain.rules;

import java.util.List;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.LogEntry;

public class LogEntryContentRule implements ValidationRule<LogEntry> {

    @Override
    public void validate(LogEntry logEntry, List<String> errors) {
        if (logEntry.getContent() == null || logEntry.getContent().isBlank()) {
            errors.add("Log entry content must not be blank");
        }
    }
}
