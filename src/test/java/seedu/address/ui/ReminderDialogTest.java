package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.reminder.Reminder;

class ReminderDialogTest {
    private static final Clock CLOCK = Clock.systemDefaultZone();
    @BeforeAll static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
        FxTestSupport.runOnFxThread(() -> javafx.application.Platform.setImplicitExit(false));
    }

    @Test void showIfNeeded_emptyList_doesNotShowDialog() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            ReminderDialog dialog = new ReminderDialog(null, List.of(), CLOCK, () -> { });
            assertFalse(dialog.showIfNeeded());
            assertFalse(dialog.isShowing());
        });
    }

    @Test void showIfNeeded_remindersExist_showsNumberedDialogWithFocusedCloseAction() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            Stage owner = new Stage();
            owner.setScene(new Scene(new StackPane()));
            owner.show();
            ReminderDialog dialog = new ReminderDialog(owner,
                    List.of(new Reminder(1, "First"), new Reminder(2, "Second")), CLOCK, () -> { });

            assertTrue(dialog.showIfNeeded());
            assertTrue(dialog.isShowing());
            assertEquals("[ID 1] First [EVERY STARTUP]" + System.lineSeparator()
                            + "[ID 2] Second [EVERY STARTUP]",
                    dialog.getAlert().getContentText());
            Button closeButton = (Button) dialog.getAlert().getDialogPane().lookupButton(ButtonType.CLOSE);
            assertTrue(closeButton.isDefaultButton());
            assertTrue(closeButton.isCancelButton());

            dialog.getAlert().close();
            owner.close();
        });
    }

    @Test void formatReminders_allStatusesIncludesFarFutureAndUsesUrgencyOrder() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-10-20T04:00:00Z"), ZoneId.of("Asia/Singapore"));
        List<Reminder> reminders = List.of(
                new Reminder(1, "Untimed"),
                new Reminder(2, "Far future", LocalDateTime.of(2026, 12, 1, 9, 0)),
                new Reminder(3, "Due soon", LocalDateTime.of(2026, 10, 20, 13, 0)),
                new Reminder(4, "Overdue", LocalDateTime.of(2026, 10, 20, 11, 59)));

        assertEquals("[ID 4] Overdue [2026-10-20 11:59] [OVERDUE]" + System.lineSeparator()
                + "[ID 3] Due soon [2026-10-20 13:00] [DUE SOON]" + System.lineSeparator()
                + "[ID 2] Far future [2026-12-01 09:00] [UPCOMING]" + System.lineSeparator()
                + "[ID 1] Untimed [EVERY STARTUP]", ReminderDialog.formatReminders(reminders, fixedClock));
    }

    @Test void closeButton_dismissesWithoutDeletingReminders() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            Model model = new ModelManager();
            model.addReminder("Keep me", null);
            Stage owner = new Stage();
            owner.setScene(new Scene(new StackPane()));
            owner.show();
            ReminderDialog dialog = new ReminderDialog(owner, model.getReminderList(), CLOCK, () -> { });
            dialog.showIfNeeded();

            Button closeButton = (Button) dialog.getAlert().getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.fire();

            assertFalse(dialog.isShowing());
            assertEquals(List.of(new Reminder(1, "Keep me")), model.getReminderList());
            owner.close();
        });
    }

    @Test void escape_dismissesAndRunsFocusCallback() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            AtomicBoolean dismissed = new AtomicBoolean();
            Stage owner = new Stage();
            owner.setScene(new Scene(new StackPane()));
            owner.show();
            ReminderDialog dialog = new ReminderDialog(owner, List.of(new Reminder(1, "First")), CLOCK, () ->
                    dismissed.set(true));
            dialog.showIfNeeded();

            Event.fireEvent(dialog.getAlert().getDialogPane(), keyEvent(KeyEvent.KEY_PRESSED, KeyCode.ESCAPE));

            assertFalse(dialog.isShowing());
            assertTrue(dismissed.get());
            owner.close();
        });
    }

    @Test void enter_activatesInitiallyDefaultCloseButton() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            Stage owner = new Stage();
            owner.setScene(new Scene(new StackPane()));
            owner.show();
            ReminderDialog dialog = new ReminderDialog(owner, List.of(new Reminder(1, "First")), CLOCK, () -> { });
            dialog.showIfNeeded();
            Button closeButton = (Button) dialog.getAlert().getDialogPane().lookupButton(ButtonType.CLOSE);

            Event.fireEvent(closeButton, keyEvent(KeyEvent.KEY_PRESSED, KeyCode.ENTER));
            Event.fireEvent(closeButton, keyEvent(KeyEvent.KEY_RELEASED, KeyCode.ENTER));

            assertFalse(dialog.isShowing());
            owner.close();
        });
    }

    @Test void manyReminders_contentScrollsAndIsKeyboardFocusable() throws Exception {
        List<Reminder> reminders = java.util.stream.IntStream.rangeClosed(1, 30)
                .mapToObj(id -> new Reminder(id, "Reminder " + id))
                .toList();

        checkReminderScroll(reminders, scroll -> {
            assertTrue(scroll.isFocusTraversable());
            assertEquals(ReminderDialog.MAX_VISIBLE_HEIGHT, scroll.getPrefViewportHeight(), 0.001);
            assertTrue(scroll.getContent().prefHeight(-1) > scroll.getPrefViewportHeight());
        });
    }

    @Test void fewReminders_contentIsNotPaddedToMaximumHeight() throws Exception {
        checkReminderScroll(List.of(new Reminder(1, "First")), scroll ->
                assertTrue(scroll.getPrefViewportHeight() < ReminderDialog.MAX_VISIBLE_HEIGHT));
    }

    /**
     * Shows a dialog listing {@code reminders}, runs {@code check} on its scrollable content, then closes it.
     */
    private static void checkReminderScroll(List<Reminder> reminders, Consumer<ScrollPane> check)
            throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            Stage owner = new Stage();
            owner.setScene(new Scene(new StackPane()));
            owner.show();
            ReminderDialog dialog = new ReminderDialog(owner, reminders, CLOCK, () -> { });
            dialog.showIfNeeded();
            try {
                check.accept((ScrollPane) dialog.getAlert().getDialogPane().lookup("#reminderScroll"));
            } finally {
                dialog.getAlert().close();
                owner.close();
            }
        });
    }

    private static KeyEvent keyEvent(javafx.event.EventType<KeyEvent> type, KeyCode code) {
        return new KeyEvent(type, "", "", code, false, false, false, false);
    }
}
