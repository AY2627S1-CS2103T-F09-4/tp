package seedu.address.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import seedu.address.logic.Logic;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;

/** Card for a parent contact, including a clickable linked-student chip. */
public class ParentContactCard extends UiPart<HBox> {
    public ParentContactCard(ParentContact contact, int displayedIndex, Logic logic, Runnable selectStudent) {
        super("ParentContactCard.fxml");
        HBox root = getRoot();
        root.setSpacing(0);
        VBox photoBox;
        ImageView photo = new ImageView();
        photo.setFitHeight(72); photo.setFitWidth(72); photo.setPreserveRatio(true);
        if (contact.getPhotoPath().isPresent()) {
            try {
                Image image = new Image(Path.of(contact.getPhotoPath().value).toUri().toString(), 72, 72, true, true, true);
                if (!image.isError()) photo.setImage(image);
            } catch (InvalidPathException ignored) { }
        }
        if (photo.getImage() == null) photo.setImage(null);
        photoBox = new VBox(4);
        photoBox.setAlignment(Pos.CENTER);
        photoBox.setMinHeight(105);
        photoBox.setPrefWidth(90);
        photoBox.setPadding(new Insets(5, 5, 5, 10));
        photoBox.getChildren().add(photo);
        if (photo.getImage() == null) {
            Label placeholder = new Label("No photo");
            placeholder.getStyleClass().add("cell_small_label");
            photoBox.getChildren().add(placeholder);
        }
        VBox details = new VBox(4);
        details.setAlignment(Pos.CENTER_LEFT);
        details.setMinHeight(105);
        details.setPadding(new Insets(5, 5, 5, 15));
        root.getChildren().addAll(photoBox, details);
        HBox nameRow = new HBox(6);
        Label index = new Label(displayedIndex + ".");
        index.getStyleClass().add("cell_big_label");
        Label name = new Label(contact.getName().fullName);
        name.getStyleClass().add("cell_big_label");
        nameRow.getChildren().addAll(index, name);
        details.getChildren().add(nameRow);
        Label phone = new Label("Phone: " + contact.getPhone().value);
        phone.getStyleClass().add("cell_small_label");
        details.getChildren().add(phone);
        if (contact.getEmail() != null) {
            Label email = new Label("Email: " + contact.getEmail().value);
            email.getStyleClass().add("cell_small_label");
            details.getChildren().add(email);
        }
        if (!contact.getRequirements().isEmpty()) {
            Label requirements = new Label("Requirements: " + String.join(", ", contact.getRequirements()));
            requirements.getStyleClass().add("cell_small_label");
            details.getChildren().add(requirements);
        }
        if (contact.getLinkedStudentId() != null) {
            for (Person student : logic.getFilteredPersonList()) {
                String id = student.getName().fullName + "|" + student.getPhone().value;
                if (id.equals(contact.getLinkedStudentId())) {
                    Button chip = new Button(student.getName().fullName);
                    chip.getStyleClass().add("student-chip");
                    chip.setOnAction(event -> selectStudent.run());
                    details.getChildren().add(new HBox(new Label("Student: "), chip));
                    break;
                }
            }
        }
    }
}
