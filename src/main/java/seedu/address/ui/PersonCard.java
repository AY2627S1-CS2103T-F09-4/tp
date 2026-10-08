package seedu.address.ui;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

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

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label subject;
    @FXML
    private Label gradeLevel;
    @FXML
    private ImageView photo;
    @FXML
    private Label photoPlaceholder;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        subject.setText("Subject: " + person.getSubject().value);
        gradeLevel.setText("Grade: " + person.getGradeLevel().value);
        gradeLevel.setVisible(person.getGradeLevel().isPresent());
        gradeLevel.setManaged(person.getGradeLevel().isPresent());
        setPhoto(person);
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
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
}
