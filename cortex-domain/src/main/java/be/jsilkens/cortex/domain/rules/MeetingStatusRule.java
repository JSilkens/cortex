package be.jsilkens.cortex.domain.rules;

import java.util.List;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.Meeting;

public class MeetingStatusRule implements ValidationRule<Meeting> {

    @Override
    public void validate(Meeting meeting, List<String> errors) {
        if (meeting.getStatus() == null) {
            errors.add("Meeting status must not be null");
        }
    }
}
