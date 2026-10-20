package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import seedu.address.model.reminder.Reminder;

class ReminderAlertSchedulerTest {
    @BeforeAll static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
        FxTestSupport.runOnFxThread(() -> javafx.application.Platform.setImplicitExit(false));
    }

    @Test void checkNow_dueMinuteReached_alertsOnceAndGroupsSimultaneousReminders() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            MutableClock clock = new MutableClock(LocalDateTime.of(2026, 10, 20, 12, 0));
            List<Reminder> reminders = List.of(
                    new Reminder(1, "First", LocalDateTime.of(2026, 10, 20, 12, 1)),
                    new Reminder(2, "Second", LocalDateTime.of(2026, 10, 20, 12, 1)),
                    new Reminder(3, "Later", LocalDateTime.of(2026, 10, 20, 12, 2)));
            List<List<Reminder>> alerts = new ArrayList<>();
            ReminderAlertScheduler scheduler = new ReminderAlertScheduler(() -> reminders, due -> {
                alerts.add(due);
                return true;
            }, clock);

            scheduler.checkNow();
            assertTrue(alerts.isEmpty());
            clock.set(LocalDateTime.of(2026, 10, 20, 12, 1));
            scheduler.checkNow();
            scheduler.checkNow();
            assertEquals(List.of(List.of(reminders.get(0), reminders.get(1))), alerts);
            clock.set(LocalDateTime.of(2026, 10, 20, 12, 2));
            scheduler.checkNow();
            assertEquals(List.of(reminders.get(2)), alerts.get(1));
        });
    }

    @Test void checkNow_dialogUnavailable_doesNotLosePendingAlert() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            MutableClock clock = new MutableClock(LocalDateTime.of(2026, 10, 20, 12, 0));
            Reminder reminder = new Reminder(1, "Due", LocalDateTime.of(2026, 10, 20, 12, 1));
            AtomicBoolean available = new AtomicBoolean();
            List<List<Reminder>> shown = new ArrayList<>();
            ReminderAlertScheduler scheduler = new ReminderAlertScheduler(() -> List.of(reminder), due -> {
                if (available.get()) {
                    shown.add(due);
                }
                return available.get();
            }, clock);
            clock.set(LocalDateTime.of(2026, 10, 20, 12, 1));

            scheduler.checkNow();
            available.set(true);
            scheduler.checkNow();

            assertEquals(List.of(List.of(reminder)), shown);
        });
    }

    @Test void startAndStop_controlsTimelineLifecycle() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            MutableClock clock = new MutableClock(LocalDateTime.of(2026, 10, 20, 12, 0));
            ReminderAlertScheduler scheduler = new ReminderAlertScheduler(List::of, ignored -> true, clock);
            assertFalse(scheduler.isRunning());
            scheduler.start();
            assertTrue(scheduler.isRunning());
            scheduler.stop();
            assertFalse(scheduler.isRunning());
        });
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(LocalDateTime time) {
            set(time);
        }

        private void set(LocalDateTime time) {
            instant = time.toInstant(ZoneOffset.UTC);
        }

        @Override public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override public Clock withZone(ZoneId zone) {
            return Clock.fixed(instant, zone);
        }

        @Override public Instant instant() {
            return instant;
        }
    }
}
