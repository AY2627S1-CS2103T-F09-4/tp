package seedu.address.ui;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;

/**
 * A coloured card showing the names, subjects, grade levels and addresses of a session's students,
 * with its time slot and displayed session number. Sized to fit within a 30-minute card.
 * A group session is shown as one card listing all of its students.
 */
class ScheduleSessionCard extends VBox {

    private static final String TEXT_STYLE = "-fx-font-size: 10px; -fx-text-fill: #1d1d1d;";

    ScheduleSessionCard(ScheduledSession session, String colour, String title, int number) {
        List<Person> students = session.getStudents();
        assert !students.isEmpty() : "a scheduled session always has a student";
        setPadding(new Insets(2, 4, 2, 4));
        setSpacing(0);
        setStyle("-fx-background-color: " + colour + "; -fx-border-color: #1d1d1d; -fx-border-width: 1;");
        setMinSize(0, 0);

        String heading = students.size() == 1 ? "Student: " : "Students: ";
        Label name = createLabel(heading + title, TEXT_STYLE + " -fx-font-weight: bold;");
        Label subjectAndGrade = createLabel(joinDistinct(students, student -> student.getSubject().value)
                + " | " + joinDistinct(students, student -> student.getGradeLevel().value), TEXT_STYLE);
        Label address = createLabel(joinDistinct(students, student -> student.getAddress().value), TEXT_STYLE);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label time = createLabel(session.getTimeSlot().start + " - " + session.getTimeSlot().end,
                "-fx-font-size: 9px; -fx-text-fill: #1d1d1d;");
        time.setAlignment(Pos.BOTTOM_RIGHT);

        getChildren().addAll(name, subjectAndGrade, address, spacer, time);

        Label sessionNumber = createLabel("Session #" + number, TEXT_STYLE + " -fx-font-weight: bold;");
        sessionNumber.setId("sessionNumber");
        getChildren().add(sessionNumber);
    }

    /** Returns the distinct values of {@code field} across {@code students}, joined by commas. */
    private static String joinDistinct(List<Person> students, Function<Person, String> field) {
        return students.stream().map(field).distinct().collect(Collectors.joining(", "));
    }

    private static Label createLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        label.setMinHeight(0);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }
}
