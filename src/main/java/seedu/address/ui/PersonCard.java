package seedu.address.ui;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import seedu.address.logic.parser.MatchRange;
import seedu.address.logic.parser.PersonAttributesTextIndexFinder;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskStatus;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;
    private final String keyword;

    @FXML
    private HBox cardPane;
    @FXML
    private TextFlow name;
    @FXML
    private Label id;
    @FXML
    private TextFlow phone;
    @FXML
    private TextFlow address;
    @FXML
    private TextFlow email;
    @FXML
    private TextFlow subject;
    @FXML
    private TextFlow gradeLevel;
    @FXML
    private ImageView photo;
    @FXML
    private Label photoPlaceholder;
    @FXML
    private FlowPane tags;
    @FXML
    private ScrollPane taskScrollPane;
    @FXML
    private VBox tasks;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex, String keyword) {
        super(FXML);
        this.person = person;
        this.keyword = keyword == null ? "" : keyword;

        PersonAttributesTextIndexFinder finder =
                new PersonAttributesTextIndexFinder(person, this.keyword);

        id.setText(displayedIndex + ". ");

        setAttributes(person, finder);

        setPhoto(person);
        taskScrollPane.setVvalue(1.0);
    }

    private void setAttributes(Person person, PersonAttributesTextIndexFinder finder) {
        name.getChildren().setAll(
                createHighlightedText(
                        person.getName().fullName,
                        finder.getNameIndex()).getChildren());

        phone.getChildren().setAll(
                createHighlightedText(
                        person.getPhone().value,
                        finder.getPhoneIndex()).getChildren());

        address.getChildren().setAll(
                createHighlightedText(
                        person.getAddress().value,
                        finder.getAddressIndex()).getChildren());

        email.getChildren().setAll(
                createHighlightedText(
                        person.getEmail().value,
                        finder.getEmailIndex()).getChildren());

        subject.getChildren().setAll(
                createHighlightedText(
                        person.getSubject().value,
                        finder.getSubjectIndex()).getChildren());

        gradeLevel.setVisible(person.getGradeLevel().isPresent());
        gradeLevel.setManaged(person.getGradeLevel().isPresent());

        if (person.getGradeLevel().isPresent()) {
            gradeLevel.getChildren().setAll(
                    createHighlightedText(
                            person.getGradeLevel().value,
                            finder.getGradeLevelIndex()).getChildren());
        }

        for (int i = 0; i < person.getTaskList().getTasks().size(); i++) {
            Task task = person.getTaskList().getTasks().get(i);

            tasks.getChildren().add(
                    createTaskRow(i + 1, task, finder.getTaskIndex(i)));
        }
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> {
                    TextFlow tagText = createHighlightedText(
                            tag.tagName, finder.getTagIndex(tag));

                    tags.getChildren().add(tagText);
                });
    }

    private HBox createTaskRow(int taskId, Task task, List<MatchRange> ranges) {
        // The number sits outside the text so highlight ranges stay aligned with the description.
        Label number = new Label(taskId + ".");
        number.getStyleClass().add("task-number");
        number.setMinWidth(Region.USE_PREF_SIZE);

        TextFlow taskText = createHighlightedText(task.description, ranges);
        taskText.getStyleClass().add("task-item");
        taskText.getStyleClass().add(task.status == TaskStatus.DONE ? "task-done" : "task-not-done");
        taskText.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(taskText, Priority.ALWAYS);

        HBox row = new HBox(4, number, taskText);
        row.getStyleClass().add("task-row");
        return row;
    }

    private void setPhoto(Person person) {
        if (!person.getPhotoPath().isPresent()) {
            showPhotoPlaceholder();
            return;
        }

        Image image;
        try {
            image = new Image(Path.of(person.getPhotoPath().value).toUri().toString(), 72, 72, true, true, true);
        } catch (InvalidPathException ipe) {
            showPhotoPlaceholder();
            return;
        }
        if (image.isError()) {
            showPhotoPlaceholder();
            return;
        }

        photo.setImage(image);
        photo.setVisible(true);
        photo.setManaged(true);
        photoPlaceholder.setVisible(false);
        photoPlaceholder.setManaged(false);
    }

    private void showPhotoPlaceholder() {
        photo.setImage(null);
        photo.setVisible(false);
        photo.setManaged(false);
        photoPlaceholder.setVisible(true);
        photoPlaceholder.setManaged(true);
    }

    private TextFlow createHighlightedText(
            String text, List<MatchRange> ranges) {

        TextFlow textFlow = new TextFlow();
        int currentIndex = 0;

        if (text == null || text.isEmpty()) {
            return textFlow;
        }

        currentIndex = colorStartingAndMatchingText(text, ranges, currentIndex, textFlow);

        // Ordinary text after the final match.
        if (currentIndex < text.length()) {
            Text normalText = new Text(text.substring(currentIndex));
            normalText.getStyleClass().add("normal-card-text");
            textFlow.getChildren().add(normalText);
        }

        return textFlow;
    }

    private int colorStartingAndMatchingText(String text, List<MatchRange> ranges, int currentIndex,
                                             TextFlow textFlow) {
        for (MatchRange range : ranges) {
            int start = range.getStart();
            int end = range.getEnd();

            if (start < currentIndex || start < 0
                    || end <= start || end > text.length()) {
                continue;
            }

            // Ordinary text before the match.
            if (currentIndex < start) {
                Text normalText =
                        new Text(text.substring(currentIndex, start));
                normalText.getStyleClass().add("normal-card-text");
                textFlow.getChildren().add(normalText);
            }

            if (!keyword.isEmpty()) {
                // Matching text.
                Text match = new Text(text.substring(start, end));
                match.getStyleClass().add("search-match");
                textFlow.getChildren().add(match);

                currentIndex = end;
            }
        }
        return currentIndex;
    }

}
