package seedu.address.ui;

import java.time.Clock;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.reminder.ReminderDisplay;

/**
 * Startup dialog that displays persistent tutor-level reminders. Long lists scroll; the list can be focused
 * with Tab and scrolled with the arrow keys.
 */
class ReminderDialog {
    static final String TITLE = "Tutoria reminders";
    static final String HEADER = "Your reminders";
    static final int MAX_VISIBLE_HEIGHT = 300;
    private static final int CONTENT_WIDTH = 480;

    private static final Logger logger = LogsCenter.getLogger(ReminderDialog.class);

    private final List<Reminder> reminders;
    private final Stage owner;
    private final Runnable onDismiss;
    private final Clock clock;
    private final String header;
    private Alert alert;

    ReminderDialog(Stage owner, List<Reminder> reminders, Clock clock, Runnable onDismiss) {
        this(owner, reminders, clock, HEADER, onDismiss);
    }

    ReminderDialog(Stage owner, List<Reminder> reminders, Clock clock, String header, Runnable onDismiss) {
        this.owner = owner;
        this.reminders = List.copyOf(reminders);
        this.clock = clock;
        this.header = header;
        this.onDismiss = onDismiss;
    }

    /** Shows the dialog if at least one reminder exists. Returns whether it was shown. */
    boolean showIfNeeded() {
        if (reminders.isEmpty()) {
            return false;
        }
        alert = new Alert(Alert.AlertType.INFORMATION, formatReminders(reminders, clock), ButtonType.CLOSE);
        alert.initOwner(owner);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(TITLE);
        alert.setHeaderText(header);
        alert.getDialogPane().getStylesheets().add("view/DarkTheme.css");
        alert.getDialogPane().setContent(createScrollableContent(alert.getContentText()));

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
        logger.info("Showing " + reminders.size() + " reminder(s).");
        return true;
    }

    /**
     * Wraps {@code text} in a scroll pane that grows with the text up to {@code MAX_VISIBLE_HEIGHT} and
     * scrolls beyond that, so every reminder can be reached without a mouse.
     */
    private static ScrollPane createScrollableContent(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("reminder-text");
        label.setWrapText(true);
        label.setMaxWidth(CONTENT_WIDTH);

        ScrollPane scrollPane = new ScrollPane(label);
        scrollPane.getStyleClass().add("reminder-scroll-pane");
        scrollPane.setId("reminderScroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFocusTraversable(true);
        scrollPane.setPrefViewportWidth(CONTENT_WIDTH);
        scrollPane.prefViewportHeightProperty().bind(Bindings.createDoubleBinding(() ->
                Math.min(MAX_VISIBLE_HEIGHT, label.prefHeight(CONTENT_WIDTH)), label.textProperty(),
                label.fontProperty(), label.skinProperty()));
        return scrollPane;
    }

    static String formatReminders(List<Reminder> reminders, Clock clock) {
        return ReminderDisplay.sorted(reminders, clock).stream()
                .map(reminder -> ReminderDisplay.format(reminder, clock))
                .collect(java.util.stream.Collectors.joining(System.lineSeparator()));
    }

    boolean isShowing() {
        return alert != null && alert.isShowing();
    }

    Alert getAlert() {
        return alert;
    }
}
