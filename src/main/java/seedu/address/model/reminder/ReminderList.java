package seedu.address.model.reminder;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** An immutable, ordered list of tutor-level reminders. */
public final class ReminderList {
    private final List<Reminder> reminders;

    public ReminderList() {
        this(List.of());
    }

    /** Creates a reminder list containing {@code reminders} in order. */
    public ReminderList(List<Reminder> reminders) {
        requireNonNull(reminders);
        this.reminders = reminders.stream().map(reminder -> {
            Objects.requireNonNull(reminder);
            return reminder.copy();
        }).toList();
        if (this.reminders.stream().map(Reminder::getId).distinct().count() != this.reminders.size()) {
            throw new IllegalArgumentException("Reminder IDs must be unique.");
        }
    }

    public List<Reminder> getReminders() {
        return Collections.unmodifiableList(reminders);
    }

    public int size() {
        return reminders.size();
    }

    public Reminder getReminderById(int id) {
        return reminders.stream().filter(reminder -> reminder.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown reminder ID: " + id));
    }

    public boolean containsId(int id) {
        return reminders.stream().anyMatch(reminder -> reminder.getId() == id);
    }

    /** Returns a new list with {@code reminder} appended. */
    public ReminderList withReminder(Reminder reminder) {
        requireNonNull(reminder);
        if (containsId(reminder.getId())) {
            throw new IllegalArgumentException("Reminder IDs must be unique.");
        }
        List<Reminder> updated = new ArrayList<>(reminders);
        updated.add(reminder);
        return new ReminderList(updated);
    }

    /** Returns a new list without the reminder identified by {@code id}. */
    public ReminderList withoutReminderId(int id) {
        if (!containsId(id)) {
            throw new IllegalArgumentException("Unknown reminder ID: " + id);
        }
        return new ReminderList(reminders.stream().filter(reminder -> reminder.getId() != id).toList());
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
