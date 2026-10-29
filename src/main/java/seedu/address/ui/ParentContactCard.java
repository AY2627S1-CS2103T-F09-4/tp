package seedu.address.ui;

import static java.util.Objects.requireNonNull;

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
        requireNonNull(contact);

        ParentAttributesTextIndexFinder finder =
                new ParentAttributesTextIndexFinder(contact, this.keyword);

        assert displayedIndex > 0 : "Displayed index must be positive";
        id.setText(displayedIndex + ". ");
        setPhoto(contact);

        if (contact.getLinkedStudentId() != null) {
            linkedStudent.setOnMouseClicked(event -> selectStudent.run());
            linkedStudent.setCursor(Cursor.HAND);
        }

        setAttributes(contact, logic, finder);
    }

    private void setAttributes(ParentContact contact, Logic logic, ParentAttributesTextIndexFinder finder) {
        requireNonNull(logic);
        setHighlightedText(name, contact.getName().fullName, finder.getNameIndex(), false);
        setHighlightedText(phone, contact.getPhone().value, finder.getPhoneIndex(), false);
        setHighlightedTextForEmail(contact, finder);
        setHighlightedTextForLinkedStudent(contact, logic, finder);
        setHighlightedTextForRequirements(contact, finder);
    }

    private void setHighlightedTextForEmail(ParentContact contact, ParentAttributesTextIndexFinder finder) {
        email.setVisible(contact.getEmail() != null);
        email.setManaged(contact.getEmail() != null);
        if (contact.getEmail() != null) {
            setHighlightedText(email, contact.getEmail().value, finder.getEmailIndex(), false);
        }
    }

    private void setHighlightedTextForLinkedStudent(ParentContact contact, Logic logic,
                                                    ParentAttributesTextIndexFinder finder) {
        String linkedStudentText = setLinkedStudent(contact, logic);

        linkedStudent.setVisible(contact.getLinkedStudentName() != null);
        linkedStudent.setManaged(contact.getLinkedStudentName() != null);
        if (contact.getLinkedStudentName() != null) {
            setHighlightedText(linkedStudent, linkedStudentText,
                    finder.getLinkedStudentIndex(16), true);
        }
    }

    private void setHighlightedTextForRequirements(ParentContact contact, ParentAttributesTextIndexFinder finder) {
        String requirementText = contact.getRequirements().isEmpty()
                ? "Requirements: None"
                : "Requirements: " + String.join(", ", contact.getRequirements());
        setHighlightedText(requirements, requirementText, finder.getRequirementsIndex(14), false);
    }

    private static String setLinkedStudent(ParentContact contact, Logic logic) {
        requireNonNull(contact);
        requireNonNull(logic);
        String linkedStudentText = "Linked student: N/A";
        if (contact.getLinkedStudentId() == null) {
            return linkedStudentText;
        }
        assert logic.getPersonList() != null : "Parent list must not be null";
        for (Person student : logic.getPersonList()) {
            String studentId = student.getStudentId();
            if (studentId.equals(contact.getLinkedStudentId())) {
                linkedStudentText = "Linked student: " + student.getName().fullName;
                break;
            }
        }
        return linkedStudentText;
    }

    private void setHighlightedText(TextFlow attribute, String text, List<MatchRange> ranges, boolean isLinkedStudent) {
        attribute.getChildren().setAll(createHighlightedText(text, ranges, isLinkedStudent).getChildren());
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

    private TextFlow createHighlightedText(
            String text, List<MatchRange> ranges, boolean isLinkedStudent) {

        TextFlow textFlow = new TextFlow();
        int currentIndex = 0;

        if (text == null || text.isEmpty() || (isLinkedStudent && this.linkedStudent == null)) {
            return textFlow;
        }

        assert ranges != null : "Match ranges must not be null";
        for (MatchRange range : ranges) {
            int start = range.getStart();
            int end = range.getEnd();

            if (start < currentIndex || start < 0
                    || end <= start || end > text.length()) {
                continue;
            }

            setTextBeforeMatch(text, isLinkedStudent, currentIndex, start, textFlow);

            currentIndex = setTextAtMatch(text, isLinkedStudent, start, end, textFlow, currentIndex);
        }
        setTextAfterMatch(text, isLinkedStudent, currentIndex, textFlow);

        return textFlow;
    }

    private static void setTextAfterMatch(String text, boolean isLinkedStudent, int currentIndex, TextFlow textFlow) {
        if (currentIndex < text.length()) {
            Text normalText = new Text(text.substring(currentIndex));
            normalText.getStyleClass().add("normal-card-text");
            if (isLinkedStudent) {
                normalText.getStyleClass().add("linked-card-text");
                normalText.setFill(Color.web("#1a73e8"));
                normalText.setUnderline(true);
            }
            textFlow.getChildren().add(normalText);
        }
    }

    private int setTextAtMatch(String text, boolean isLinkedStudent, int start, int end,
                               TextFlow textFlow, int currentIndex) {
        if (!keyword.isEmpty()) {
            Text match = new Text(text.substring(start, end));
            match.getStyleClass().add("search-match");
            if (isLinkedStudent) {
                match.setUnderline(true);
            }
            textFlow.getChildren().add(match);

            currentIndex = end;
        }
        return currentIndex;
    }

    private static void setTextBeforeMatch(String text, boolean isLinkedStudent, int currentIndex,
                                           int start, TextFlow textFlow) {
        if (currentIndex < start) {
            Text normalText =
                    new Text(text.substring(currentIndex, start));
            normalText.getStyleClass().add("normal-card-text");
            if (isLinkedStudent) {
                normalText.getStyleClass().add("linked-card-text");
                normalText.setFill(Color.web("#1a73e8"));
                normalText.setUnderline(true);
            }
            textFlow.getChildren().add(normalText);
        }
    }
}
