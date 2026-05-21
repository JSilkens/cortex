package be.jsilkens.cortex.domain.rules;

import java.util.List;

import be.jsilkens.cortex.common.domain.validation.ValidationRule;
import be.jsilkens.cortex.domain.Meeting;

public class MeetingTitleRule implements ValidationRule<Meeting> {

    @Override
    public void validate(Meeting meeting, List<String> errors) {
        if (meeting.getTitle() == null || meeting.getTitle().isBlank()) {
            errors.add("Meeting title must not be blank");
        }
    }
}
