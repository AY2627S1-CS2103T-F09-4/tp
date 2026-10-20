package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.reminder.ReminderDisplay;

/** Checks for reminders reaching their due minute while the application remains open. */
class ReminderAlertScheduler {
    private final Supplier<List<Reminder>> remindersSupplier;
    private final Function<List<Reminder>, Boolean> alertConsumer;
    private final Clock clock;
    private final Set<Integer> alertedIds = new HashSet<>();
    private final Timeline timeline;

    ReminderAlertScheduler(Supplier<List<Reminder>> remindersSupplier,
            Function<List<Reminder>, Boolean> alertConsumer, Clock clock) {
        this.remindersSupplier = requireNonNull(remindersSupplier);
        this.alertConsumer = requireNonNull(alertConsumer);
        this.clock = requireNonNull(clock);
        LocalDateTime now = currentMinute();
        remindersSupplier.get().stream().filter(reminder -> reminder.getDueAt()
                .map(due -> !due.isAfter(now)).orElse(false)).map(Reminder::getId).forEach(alertedIds::add);
        timeline = new Timeline(new KeyFrame(Duration.seconds(20), event -> checkNow()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    void start() {
        timeline.play();
    }

    void stop() {
        timeline.stop();
    }

    boolean isRunning() {
        return timeline.getStatus() == javafx.animation.Animation.Status.RUNNING;
    }

    void checkNow() {
        LocalDateTime now = currentMinute();
        List<Reminder> due = ReminderDisplay.sorted(remindersSupplier.get().stream()
                .filter(reminder -> !alertedIds.contains(reminder.getId()))
                .filter(reminder -> reminder.getDueAt().map(value -> !value.isAfter(now)).orElse(false))
                .toList(), clock);
        if (!due.isEmpty() && alertConsumer.apply(due)) {
            due.stream().map(Reminder::getId).forEach(alertedIds::add);
        }
    }

    private LocalDateTime currentMinute() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES);
    }
}
