package seedu.address.model.reminder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

class ReminderDisplayTest {
    private static final ZoneId ZONE = ZoneId.of("Asia/Singapore");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-20T04:00:45Z"), ZONE);
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 20, 12, 0);

    @Test void classify_allBoundaries() {
        assertEquals(ReminderUrgency.EVERY_STARTUP, classify(null));
        assertEquals(ReminderUrgency.OVERDUE, classify(NOW.minusMinutes(1)));
        assertEquals(ReminderUrgency.DUE_SOON, classify(NOW));
        assertEquals(ReminderUrgency.DUE_SOON, classify(NOW.plusMinutes(1)));
        assertEquals(ReminderUrgency.DUE_SOON, classify(NOW.plusHours(24)));
        assertEquals(ReminderUrgency.UPCOMING, classify(NOW.plusHours(24).plusMinutes(1)));
    }

    @Test void classify_monthYearAndLeapDayBoundaries() {
        Clock yearEnd = Clock.fixed(Instant.parse("2026-12-31T15:59:00Z"), ZONE);
        assertEquals(ReminderUrgency.DUE_SOON, ReminderDisplay.classify(
                new Reminder(1, "New year", LocalDateTime.of(2027, 1, 1, 0, 0)), yearEnd));
        Clock leapDay = Clock.fixed(Instant.parse("2028-02-28T16:00:00Z"), ZONE);
        assertEquals(ReminderUrgency.DUE_SOON, ReminderDisplay.classify(
                new Reminder(2, "Leap day", LocalDateTime.of(2028, 2, 29, 0, 0)), leapDay));
    }

    @Test void sorted_urgencyDueTimeIdAndUntimedIdOrder() {
        Reminder untimedHigh = new Reminder(9, "Untimed high");
        Reminder upcoming = new Reminder(4, "Upcoming", NOW.plusDays(2));
        Reminder dueSoonLater = new Reminder(3, "Due later", NOW.plusHours(2));
        Reminder overdue = new Reminder(8, "Overdue", NOW.minusHours(2));
        Reminder dueSoonTieHigh = new Reminder(7, "Tie high", NOW.plusHours(1));
        Reminder dueSoonTieLow = new Reminder(2, "Tie low", NOW.plusHours(1));
        Reminder untimedLow = new Reminder(1, "Untimed low");

        assertEquals(List.of(overdue, dueSoonTieLow, dueSoonTieHigh, dueSoonLater, upcoming,
                untimedLow, untimedHigh), ReminderDisplay.sorted(List.of(untimedHigh, upcoming, dueSoonLater,
                overdue, dueSoonTieHigh, dueSoonTieLow, untimedLow), CLOCK));
    }

    @Test void sorted_timeAdvances_orderChangesButIdsDoNot() {
        Reminder first = new Reminder(1, "First", NOW.plusHours(25));
        Reminder second = new Reminder(2, "Second", NOW.plusHours(2));
        Clock later = Clock.offset(CLOCK, java.time.Duration.ofHours(26));
        assertEquals(List.of(second, first), ReminderDisplay.sorted(List.of(first, second), CLOCK));
        assertEquals(List.of(second, first), ReminderDisplay.sorted(List.of(first, second), later));
        assertEquals(List.of(1, 2), List.of(first.getId(), second.getId()));
    }

    @Test void format_includesStableIdDueAndStatus() {
        assertEquals("[ID 3] Call parent [2026-10-20 13:00] [DUE SOON]",
                ReminderDisplay.format(new Reminder(3, "Call parent", NOW.plusHours(1)), CLOCK));
        assertEquals("[ID 4] Collect worksheets [EVERY STARTUP]",
                ReminderDisplay.format(new Reminder(4, "Collect worksheets"), CLOCK));
    }

    private static ReminderUrgency classify(LocalDateTime dueAt) {
        return ReminderDisplay.classify(new Reminder(1, "Test", dueAt), CLOCK);
    }
}
