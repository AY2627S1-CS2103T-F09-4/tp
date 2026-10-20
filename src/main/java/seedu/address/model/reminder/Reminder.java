package seedu.address.model.reminder;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;

/** Represents a tutor-level reminder. */
public class Reminder {
    public static final String MESSAGE_CONSTRAINTS = "Reminder text should not be blank.";
    public final String text;

    /** Creates a reminder with trimmed, non-blank text. */
    public Reminder(String text) {
        requireNonNull(text);
        String trimmedText = text.trim();
        checkArgument(isValidText(trimmedText), MESSAGE_CONSTRAINTS);
        this.text = trimmedText;
    }

    /** Returns true if {@code text} contains at least one non-whitespace character. */
    public static boolean isValidText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof Reminder otherReminder && text.equals(otherReminder.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
