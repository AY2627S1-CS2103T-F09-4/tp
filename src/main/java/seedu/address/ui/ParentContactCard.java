package seedu.address.ui;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.logic.Logic;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code ParentContact}.
 */
public class ParentContactCard extends UiPart<Region> {
    private static final String FXML = "ParentContactCard.fxml";

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label linkedStudent;
    @FXML
    private Label requirements;
    @FXML
    private ImageView photo;
    @FXML
    private Label photoPlaceholder;

    /**
     * Creates a {@code ParentContactCard} with the given parent contact and index to display.
     */
    public ParentContactCard(ParentContact contact, int displayedIndex, Logic logic, Runnable selectStudent) {
        super(FXML);
        id.setText(displayedIndex + ". ");
        name.setText(contact.getName().fullName);
        phone.setText(contact.getPhone().value);
        email.setText(contact.getEmail() == null ? "" : contact.getEmail().value);
        email.setVisible(contact.getEmail() != null);
        email.setManaged(contact.getEmail() != null);

        String linkedStudentText = "Linked student: N/A";
        if (contact.getLinkedStudentId() != null) {
            for (Person student : logic.getFilteredPersonList()) {
                String studentId = student.getName().fullName + "|" + student.getPhone().value;
                if (studentId.equals(contact.getLinkedStudentId())) {
                    linkedStudentText = "Linked student: " + student.getName().fullName;
                    break;
                }
            }
        }
        linkedStudent.setText(linkedStudentText);
        linkedStudent.setVisible(contact.getLinkedStudentId() != null);
        linkedStudent.setManaged(contact.getLinkedStudentId() != null);

        String requirementText = contact.getRequirements().isEmpty()
                ? "Requirements: None"
                : "Requirements: " + String.join(", ", contact.getRequirements());
        requirements.setText(requirementText);

        setPhoto(contact);

        if (contact.getLinkedStudentId() != null) {
            linkedStudent.setOnMouseClicked(event -> selectStudent.run());
            linkedStudent.setStyle("-fx-cursor: hand;");
        }
    }

    private void setPhoto(ParentContact contact) {
        if (!contact.getPhotoPath().isPresent()) {
            showPhotoPlaceholder();
            return;
        }

        Image image;
        try {
            image = new Image(Path.of(contact.getPhotoPath().value).toUri().toString(), 72, 72, true, true, true);
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
