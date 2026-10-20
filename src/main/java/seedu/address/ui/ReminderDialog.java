package seedu.address.ui;

import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seedu.address.model.reminder.Reminder;

/** Startup dialog that displays persistent tutor-level reminders. */
class ReminderDialog {
    static final String TITLE = "Tutoria reminders";
    static final String HEADER = "Your reminders";

    private final List<Reminder> reminders;
    private final Stage owner;
    private final Runnable onDismiss;
    private Alert alert;

    ReminderDialog(Stage owner, List<Reminder> reminders, Runnable onDismiss) {
        this.owner = owner;
        this.reminders = List.copyOf(reminders);
        this.onDismiss = onDismiss;
    }

    /** Shows the dialog if at least one reminder exists. Returns whether it was shown. */
    boolean showIfNeeded() {
        if (reminders.isEmpty()) {
            return false;
        }
        alert = new Alert(Alert.AlertType.INFORMATION, formatReminders(reminders), ButtonType.CLOSE);
        alert.initOwner(owner);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(TITLE);
        alert.setHeaderText(HEADER);
        alert.getDialogPane().getStylesheets().add("view/DarkTheme.css");

        Button closeButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setDefaultButton(true);
        closeButton.setCancelButton(true);
        alert.getDialogPane().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                alert.close();
                event.consume();
            }
        });
        alert.setOnShown(event -> Platform.runLater(closeButton::requestFocus));
        alert.setOnHidden(event -> onDismiss.run());
        alert.show();
        return true;
    }

    static String formatReminders(List<Reminder> reminders) {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < reminders.size(); i++) {
            if (i > 0) {
                content.append(System.lineSeparator());
            }
            content.append(i + 1).append(". ").append(reminders.get(i));
        }
        return content.toString();
    }

    boolean isShowing() {
        return alert != null && alert.isShowing();
    }

    Alert getAlert() {
        return alert;
    }
}
