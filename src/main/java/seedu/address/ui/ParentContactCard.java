package seedu.address.ui;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import seedu.address.logic.Logic;
import seedu.address.logic.parser.MatchRange;
import seedu.address.logic.parser.ParentAttributesTextIndexFinder;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code ParentContact}.
 */
public class ParentContactCard extends UiPart<Region> {
    private static final String FXML = "ParentContactCard.fxml";

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
    private TextFlow email;
    @FXML
    private TextFlow linkedStudent;
    @FXML
    private TextFlow requirements;
    @FXML
    private ImageView photo;
    @FXML
    private Label photoPlaceholder;

    /**
     * Creates a {@code ParentContactCard} with the given parent contact and index to display.
     */
    public ParentContactCard(ParentContact contact, int displayedIndex, Logic logic, Runnable selectStudent,
                             String keyword) {
        super(FXML);
        this.keyword = keyword == null ? "" : keyword;

        ParentAttributesTextIndexFinder finder =
                new ParentAttributesTextIndexFinder(contact, this.keyword);

        id.setText(displayedIndex + ". ");
        setPhoto(contact);

        if (contact.getLinkedStudentId() != null) {
            linkedStudent.setOnMouseClicked(event -> selectStudent.run());
            linkedStudent.setCursor(Cursor.HAND);
        }

        setAttributes(contact, logic, finder);
    }

    private void setAttributes(ParentContact contact, Logic logic, ParentAttributesTextIndexFinder finder) {

        name.getChildren().setAll(
                createHighlightedText(
                        contact.getName().fullName,
                        finder.getNameIndex()).getChildren());
        phone.getChildren().setAll(
                createHighlightedText(
                        contact.getPhone().value,
                        finder.getPhoneIndex()).getChildren());

        email.setVisible(contact.getEmail() != null);
        email.setManaged(contact.getEmail() != null);

        if (contact.getEmail() != null) {
            email.getChildren().setAll(
                    createHighlightedText(
                            contact.getEmail().value,
                            finder.getEmailIndex()).getChildren());
        }

        String linkedStudentText = "Linked student: N/A";
        if (contact.getLinkedStudentId() != null) {
            for (Person student : logic.getPersonList()) {
                String studentId = student.getName().fullName + "|" + student.getPhone().value;
                if (studentId.equals(contact.getLinkedStudentId())) {
                    linkedStudentText = "Linked student: " + student.getName().fullName;
                    break;
                }
            }
        }

        linkedStudent.setVisible(contact.getLinkedStudentId() != null);
        linkedStudent.setManaged(contact.getLinkedStudentId() != null);
        linkedStudent.getChildren().setAll(
                createLinkText(
                        linkedStudentText,
                        finder.getLinkedStudentIndex(16)).getChildren());

        String requirementText = contact.getRequirements().isEmpty()
                ? "Requirements: None"
                : "Requirements: " + String.join(", ", contact.getRequirements());
        requirements.getChildren().setAll(
                createHighlightedText(
                        requirementText,
                        finder.getRequirementsIndex(14)).getChildren());
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

    private TextFlow createLinkText(
            String text, List<MatchRange> ranges) {

        TextFlow textFlow = new TextFlow();
        int currentIndex = 0;

        if (text == null || text.isEmpty() || this.linkedStudent == null) {
            return textFlow;
        }

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
                normalText.getStyleClass().add("linked-card-text");
                normalText.setFill(Color.web("#1a73e8"));
                normalText.setUnderline(true);
                textFlow.getChildren().add(normalText);
            }

            if (!keyword.isEmpty()) {
                // Matching text.
                Text match = new Text(text.substring(start, end));
                match.getStyleClass().add("search-match");
                match.setUnderline(true);
                textFlow.getChildren().add(match);

                currentIndex = end;
            }
        }


        // Ordinary text after the final match.
        if (currentIndex < text.length()) {
            Text normalText = new Text(text.substring(currentIndex));
            normalText.getStyleClass().add("linked-card-text");
            normalText.setFill(Color.web("#1a73e8"));
            normalText.setUnderline(true);
            textFlow.getChildren().add(normalText);

        }

        return textFlow;
    }

    private TextFlow createHighlightedText(
            String text, List<MatchRange> ranges) {

        TextFlow textFlow = new TextFlow();
        int currentIndex = 0;

        if (text == null || text.isEmpty()) {
            return textFlow;
        }

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

        // Ordinary text after the final match.
        if (currentIndex < text.length()) {
            Text normalText = new Text(text.substring(currentIndex));
            normalText.getStyleClass().add("normal-card-text");
            textFlow.getChildren().add(normalText);
        }

        return textFlow;
    }
}
