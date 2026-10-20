package seedu.address.model.reminder;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/** Represents a tutor-level reminder. */
public final class Reminder {
    public static final String MESSAGE_ID_CONSTRAINTS = "Reminder ID must be a positive integer.";
    public static final String MESSAGE_CONSTRAINTS = "Reminder text should not be blank.";
    public static final String MESSAGE_TIME_CONSTRAINTS = "Reminder due time must use minute-level precision.";
    private final int id;
    private final String text;
    private final LocalDateTime dueAt;

    /** Creates a reminder with trimmed, non-blank text. */
    public Reminder(int id, String text, LocalDateTime dueAt) {
        requireNonNull(text);
        String trimmedText = text.trim();
        checkArgument(isValidId(id), MESSAGE_ID_CONSTRAINTS);
        checkArgument(isValidText(trimmedText), MESSAGE_CONSTRAINTS);
        checkArgument(dueAt == null || dueAt.getSecond() == 0 && dueAt.getNano() == 0, MESSAGE_TIME_CONSTRAINTS);
        this.id = id;
        this.text = trimmedText;
        this.dueAt = dueAt;
    }

    public Reminder(int id, String text) {
        this(id, text, null);
    }

    public static boolean isValidId(int id) {
        return id > 0;
    }

    /** Returns true if {@code text} contains at least one non-whitespace character. */
    public static boolean isValidText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Optional<LocalDateTime> getDueAt() {
        return Optional.ofNullable(dueAt);
    }

    public Reminder copy() {
        return new Reminder(id, text, dueAt);
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
        return other instanceof Reminder otherReminder && id == otherReminder.id
                && text.equals(otherReminder.text) && Objects.equals(dueAt, otherReminder.dueAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, dueAt);
    }
}
