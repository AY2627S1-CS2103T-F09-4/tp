package seedu.address.model.reminder;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

/** Classifies, orders, and formats reminders consistently for logic and UI. */
public final class ReminderDisplay {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    private ReminderDisplay() {}

    /** Classifies {@code reminder} relative to the current minute supplied by {@code clock}. */
    public static ReminderUrgency classify(Reminder reminder, Clock clock) {
        requireNonNull(reminder);
        requireNonNull(clock);
        if (reminder.getDueAt().isEmpty()) {
            return ReminderUrgency.EVERY_STARTUP;
        }
        LocalDateTime now = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime dueAt = reminder.getDueAt().orElseThrow();
        if (dueAt.isBefore(now)) {
            return ReminderUrgency.OVERDUE;
        }
        return dueAt.isAfter(now.plusHours(24)) ? ReminderUrgency.UPCOMING : ReminderUrgency.DUE_SOON;
    }

    /** Returns a sorted copy in urgency, due-time, then stable-ID order. */
    public static List<Reminder> sorted(List<Reminder> reminders, Clock clock) {
        requireNonNull(reminders);
        Comparator<Reminder> comparator = Comparator.comparing((Reminder reminder) -> classify(reminder, clock))
                .thenComparing(reminder -> reminder.getDueAt().orElse(LocalDateTime.MAX))
                .thenComparingInt(Reminder::getId);
        return reminders.stream().sorted(comparator).toList();
    }

    /** Formats a reminder with its stable ID, optional due time, and urgency. */
    public static String format(Reminder reminder, Clock clock) {
        String due = reminder.getDueAt().map(value -> " [" + DATE_TIME_FORMATTER.format(value) + "]").orElse("");
        return String.format("[ID %d] %s%s [%s]", reminder.getId(), reminder.getText(), due,
                classify(reminder, clock).getDisplayName());
    }
}
