package seedu.address.model.reminder;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;

/** An immutable, ordered list of tutor-level reminders. */
public class ReminderList {
    private final List<Reminder> reminders;

    public ReminderList() {
        this(List.of());
    }

    /** Creates a reminder list containing {@code reminders} in order. */
    public ReminderList(List<Reminder> reminders) {
        requireNonNull(reminders);
        this.reminders = new ArrayList<>(reminders);
        this.reminders.forEach(Objects::requireNonNull);
    }

    public List<Reminder> getReminders() {
        return Collections.unmodifiableList(reminders);
    }

    public int size() {
        return reminders.size();
    }

    public Reminder getReminder(Index index) {
        requireNonNull(index);
        return reminders.get(index.getZeroBased());
    }

    /** Returns a new list with {@code reminder} appended. */
    public ReminderList withReminder(Reminder reminder) {
        requireNonNull(reminder);
        List<Reminder> updated = new ArrayList<>(reminders);
        updated.add(reminder);
        return new ReminderList(updated);
    }

    /** Returns a new list without the reminder at {@code index}. */
    public ReminderList withoutReminder(Index index) {
        requireNonNull(index);
        List<Reminder> updated = new ArrayList<>(reminders);
        updated.remove(index.getZeroBased());
        return new ReminderList(updated);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof ReminderList otherList && reminders.equals(otherList.reminders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reminders);
    }

    @Override
    public String toString() {
        return reminders.toString();
    }
}
