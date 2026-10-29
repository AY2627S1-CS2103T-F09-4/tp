package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    private VBox tasks;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex, String keyword) {
        super(FXML);
        this.keyword = keyword == null ? "" : keyword;
        requireNonNull(person);

        PersonAttributesTextIndexFinder finder =
                new PersonAttributesTextIndexFinder(person, this.keyword);

        id.setText(displayedIndex + ". ");

        setAttributes(person, finder);

        setPhoto(person);
    }

    private void setAttributes(Person person, PersonAttributesTextIndexFinder finder) {
        setHighlightedText(name, person.getName().fullName, finder.getNameIndex());
        setHighlightedText(phone, person.getPhone().value, finder.getPhoneIndex());
        setHighlightedText(address, person.getAddress().value, finder.getAddressIndex());
        setHighlightedText(email, person.getEmail().value, finder.getEmailIndex());
        setHighlightedText(subject, person.getSubject().value, finder.getSubjectIndex());
        setHighlightedTextForGradeLevel(person, finder);
        setHighlightedTextForTasks(person, finder);
        setHighlightedTextForTags(person, finder);
    }

    private void setHighlightedTextForGradeLevel(Person person, PersonAttributesTextIndexFinder finder) {
        gradeLevel.setVisible(person.getGradeLevel().isPresent());
        gradeLevel.setManaged(person.getGradeLevel().isPresent());

        if (person.getGradeLevel().isPresent()) {
            setHighlightedText(gradeLevel, person.getGradeLevel().value, finder.getGradeLevelIndex());
        }
    }

    private void setHighlightedTextForTags(Person person, PersonAttributesTextIndexFinder finder) {
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> {
                    TextFlow tagText = createHighlightedText(
                            tag.tagName, finder.getTagIndex(tag));

                    tags.getChildren().add(tagText);
                });
    }

    private void setHighlightedTextForTasks(Person person, PersonAttributesTextIndexFinder finder) {
        for (int i = 0; i < person.getTaskList().getTasks().size(); i++) {
            Task task = person.getTaskList().getTasks().get(i);

            tasks.getChildren().add(
                    createTaskRow(i + 1, task, finder.getTaskIndex(i)));
        }
    }

    private void setHighlightedText(TextFlow attribute, String text, List<MatchRange> ranges) {
        attribute.getChildren().setAll(createHighlightedText(text, ranges).getChildren());
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
            image = new Image(Path.of(person.getPhotoPath().value).toUri().toString(), 72, 72, true, true, false);
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

        colorEndingText(text, currentIndex, textFlow);

        return textFlow;
    }

    private static void colorEndingText(String text, int currentIndex, TextFlow textFlow) {
        if (currentIndex < text.length()) {
            Text normalText = new Text(text.substring(currentIndex));
            normalText.getStyleClass().add("normal-card-text");
            textFlow.getChildren().add(normalText);
        }
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

            if (currentIndex < start) {
                Text normalText =
                        new Text(text.substring(currentIndex, start));
                normalText.getStyleClass().add("normal-card-text");
                textFlow.getChildren().add(normalText);
            }

            if (!keyword.isEmpty()) {
                Text match = new Text(text.substring(start, end));
                match.getStyleClass().add("search-match");
                textFlow.getChildren().add(match);

                currentIndex = end;
            }
        }
        return currentIndex;
    }

}
