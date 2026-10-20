package seedu.address.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.reminder.Reminder;

/** Jackson-friendly representation of a {@link Reminder}. */
class JsonAdaptedReminder {
    private final Integer id;
    private final String text;
    private final String dueAt;

    @JsonCreator
    JsonAdaptedReminder(@JsonProperty("id") Integer id, @JsonProperty("text") String text,
            @JsonProperty("dueAt") String dueAt) {
        this.id = id;
        this.text = text;
        this.dueAt = dueAt;
    }

    JsonAdaptedReminder(String text) {
        this(null, text, null);
    }

    JsonAdaptedReminder(Reminder source) {
        id = source.getId();
        text = source.getText();
        dueAt = source.getDueAt().map(value -> value.format(
                seedu.address.model.reminder.ReminderDisplay.DATE_TIME_FORMATTER)).orElse(null);
    }

    Integer getId() {
        return id;
    }

    Reminder toModelType(int resolvedId) throws IllegalValueException {
        if (!Reminder.isValidText(text)) {
            throw new IllegalValueException(Reminder.MESSAGE_CONSTRAINTS);
        }
        if (!Reminder.isValidId(resolvedId)) {
            throw new IllegalValueException(Reminder.MESSAGE_ID_CONSTRAINTS);
        }
        LocalDateTime parsedDueAt = null;
        if (dueAt != null) {
            try {
                parsedDueAt = LocalDateTime.parse(dueAt,
                        seedu.address.model.reminder.ReminderDisplay.DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new IllegalValueException("Reminder due time must use a valid YYYY-MM-DD HH:mm value.");
            }
        }
        return new Reminder(resolvedId, text, parsedDueAt);
    }
}
