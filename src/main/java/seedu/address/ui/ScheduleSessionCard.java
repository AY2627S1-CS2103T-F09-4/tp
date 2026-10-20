package seedu.address.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;

/**
 * A coloured card showing a scheduled student's name, subject, grade level and address,
 * with the time slot at the bottom right. Sized to fit within a 30-minute card.
 */
class ScheduleSessionCard extends VBox {

    private static final String TEXT_STYLE = "-fx-font-size: 10px; -fx-text-fill: #1d1d1d;";

    ScheduleSessionCard(ScheduledSession session, String colour, String title) {
        Person student = session.getStudent();
        setPadding(new Insets(2, 4, 2, 4));
        setSpacing(0);
        setStyle("-fx-background-color: " + colour + "; -fx-border-color: #1d1d1d; -fx-border-width: 1;");
        setMinSize(0, 0);

        Label name = createLabel(title, TEXT_STYLE + " -fx-font-weight: bold;");
        Label subjectAndGrade = createLabel(student.getSubject().value + " | " + student.getGradeLevel().value,
                TEXT_STYLE);
        Label address = createLabel(student.getAddress().value, TEXT_STYLE);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label time = createLabel(session.getTimeSlot().start + " - " + session.getTimeSlot().end,
                "-fx-font-size: 9px; -fx-text-fill: #1d1d1d;");
        time.setAlignment(Pos.BOTTOM_RIGHT);

        getChildren().addAll(name, subjectAndGrade, address, spacer, time);
    }

    private static Label createLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        label.setMinHeight(0);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }
}
