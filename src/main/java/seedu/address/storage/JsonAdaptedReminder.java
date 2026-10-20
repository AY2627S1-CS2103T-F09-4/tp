package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.reminder.Reminder;

/** Jackson-friendly representation of a {@link Reminder}. */
class JsonAdaptedReminder {
    private final String text;

    @JsonCreator
    JsonAdaptedReminder(@JsonProperty("text") String text) {
        this.text = text;
    }

    JsonAdaptedReminder(Reminder source) {
        text = source.text;
    }

    Reminder toModelType() throws IllegalValueException {
        if (!Reminder.isValidText(text)) {
            throw new IllegalValueException(Reminder.MESSAGE_CONSTRAINTS);
        }
        return new Reminder(text);
    }
}
