package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.reminder.Reminder;

class ReminderDialogTest {
    @BeforeAll static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
        FxTestSupport.runOnFxThread(() -> javafx.application.Platform.setImplicitExit(false));
    }

    @Test void showIfNeeded_emptyList_doesNotShowDialog() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            ReminderDialog dialog = new ReminderDialog(null, List.of(), () -> { });
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
                    List.of(new Reminder("First"), new Reminder("Second")), () -> { });

            assertTrue(dialog.showIfNeeded());
            assertTrue(dialog.isShowing());
            assertEquals("1. First" + System.lineSeparator() + "2. Second",
                    dialog.getAlert().getContentText());
            Button closeButton = (Button) dialog.getAlert().getDialogPane().lookupButton(ButtonType.CLOSE);
            assertTrue(closeButton.isDefaultButton());
            assertTrue(closeButton.isCancelButton());

            dialog.getAlert().close();
            owner.close();
        });
    }

    @Test void closeButton_dismissesWithoutDeletingReminders() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            Model model = new ModelManager();
            model.addReminder(new Reminder("Keep me"));
            Stage owner = new Stage();
            owner.setScene(new Scene(new StackPane()));
            owner.show();
            ReminderDialog dialog = new ReminderDialog(owner, model.getReminderList(), () -> { });
            dialog.showIfNeeded();

            Button closeButton = (Button) dialog.getAlert().getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.fire();

            assertFalse(dialog.isShowing());
            assertEquals(List.of(new Reminder("Keep me")), model.getReminderList());
            owner.close();
        });
    }

    @Test void escape_dismissesAndRunsFocusCallback() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            AtomicBoolean dismissed = new AtomicBoolean();
            Stage owner = new Stage();
            owner.setScene(new Scene(new StackPane()));
            owner.show();
            ReminderDialog dialog = new ReminderDialog(owner, List.of(new Reminder("First")), () ->
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
            ReminderDialog dialog = new ReminderDialog(owner, List.of(new Reminder("First")), () -> { });
            dialog.showIfNeeded();
            Button closeButton = (Button) dialog.getAlert().getDialogPane().lookupButton(ButtonType.CLOSE);

            Event.fireEvent(closeButton, keyEvent(KeyEvent.KEY_PRESSED, KeyCode.ENTER));
            Event.fireEvent(closeButton, keyEvent(KeyEvent.KEY_RELEASED, KeyCode.ENTER));

            assertFalse(dialog.isShowing());
            owner.close();
        });
    }

    private static KeyEvent keyEvent(javafx.event.EventType<KeyEvent> type, KeyCode code) {
        return new KeyEvent(type, "", "", code, false, false, false, false);
    }
}
