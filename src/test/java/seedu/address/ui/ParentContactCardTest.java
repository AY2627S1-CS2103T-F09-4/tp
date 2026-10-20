package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.testutil.ParentContactBuilder;
import seedu.address.testutil.PersonBuilder;

class ParentContactCardTest {

    @TempDir
    Path testFolder;

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
            // JavaFX toolkit has already been initialized.
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);

        assertTrue(latch.await(10, TimeUnit.SECONDS),
                "JavaFX toolkit did not initialise in time");
    }

    private static <T> T runOnFxThread(Callable<T> operation) throws Exception {
        FutureTask<T> task = new FutureTask<>(operation);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(ParentContactCard card, String fieldName)
            throws ReflectiveOperationException {
        Field field = ParentContactCard.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(card);
    }

    private static String getDisplayedText(Node node) {
        if (node instanceof Text) {
            return ((Text) node).getText();
        }

        if (node instanceof TextFlow textFlow) {
            StringBuilder result = new StringBuilder();

            for (Node child : textFlow.getChildren()) {
                result.append(getDisplayedText(child));
            }

            return result.toString();
        }

        return "";
    }

    private Logic createLogic() {
        return new LogicStub();
    }

    private static class LogicStub implements Logic {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        @Override
        public CommandResult execute(String commandText) throws CommandException, ParseException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void resetPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<ParentContact> getParentContactList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<ScheduledSession> getScheduleList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }
    }

    @Test
    void constructor_displaysParentAttributes() throws Exception {
        runOnFxThread(() -> {
            ParentContact contact = new ParentContactBuilder()
                    .withName("Alice Tan")
                    .withPhone("91234567")
                    .withEmail("alice@example.com")
                    .withRequirements("Vegetarian", "No peanuts")
                    .build();

            ParentContactCard card = new ParentContactCard(
                    contact, 1, createLogic(), () -> { }, null);
            Label id = getField(card, "id");

            assertEquals("1. ", id.getText());
            assertEquals("Alice Tan",
                    getDisplayedText(getField(card, "name")));
            assertEquals("91234567",
                    getDisplayedText(getField(card, "phone")));
            assertEquals("alice@example.com",
                    getDisplayedText(getField(card, "email")));
            assertEquals("Requirements: Vegetarian, No peanuts",
                    getDisplayedText(getField(card, "requirements")));

            return null;
        });
    }

    @Test
    void constructorWithEmail_displaysEmail() throws Exception {
        runOnFxThread(() -> {
            ParentContact contact = new ParentContactBuilder()
                    .withEmail("parent@example.com")
                    .build();

            ParentContactCard card = new ParentContactCard(
                    contact, 1, createLogic(), () -> { }, "");

            TextFlow email = getField(card, "email");

            assertTrue(email.isVisible());
            assertTrue(email.isManaged());
            assertEquals("parent@example.com",
                    getDisplayedText(email));

            return null;
        });
    }

    @Test
    void constructorWithoutLinkedStudent_hidesLinkedStudent() throws Exception {
        runOnFxThread(() -> {
            ParentContact contact = new ParentContactBuilder().build();

            ParentContactCard card = new ParentContactCard(
                    contact, 1, createLogic(), () -> { }, "");

            TextFlow linkedStudent = getField(card, "linkedStudent");

            assertFalse(linkedStudent.isVisible());
            assertFalse(linkedStudent.isManaged());

            return null;
        });
    }

    @Test
    void constructorWithLinkedStudent_displaysStudentName() throws Exception {
        runOnFxThread(() -> {
            Person student = new PersonBuilder()
                    .withName("Alice Tan")
                    .withPhone("91234567")
                    .build();

            LogicStub logic = new LogicStub();
            logic.getPersonList().add(student);

            String linkedStudentId = student.getName().fullName
                    + "|" + student.getPhone().value;

            ParentContact contact = new ParentContactBuilder()
                    .withLinkedStudentId(linkedStudentId)
                    .build();

            ParentContactCard card = new ParentContactCard(
                    contact, 1, logic, () -> { }, "");

            TextFlow linkedStudent = getField(card, "linkedStudent");

            assertTrue(linkedStudent.isVisible());
            assertTrue(linkedStudent.isManaged());
            assertEquals("Linked student: Alice Tan",
                    getDisplayedText(linkedStudent));

            return null;
        });
    }

    @Test
    void constructorWithoutRequirements_displaysNone() throws Exception {
        runOnFxThread(() -> {
            ParentContact contact = new ParentContactBuilder()
                    .withRequirements()
                    .build();

            ParentContactCard card = new ParentContactCard(
                    contact, 1, createLogic(), () -> { }, "");

            TextFlow requirements = getField(card, "requirements");

            assertEquals("Requirements: None",
                    getDisplayedText(requirements));

            return null;
        });
    }

    @Test
    void constructorWithoutPhoto_showsPlaceholder() throws Exception {
        runOnFxThread(() -> {
            ParentContact contact = new ParentContactBuilder().build();

            ParentContactCard card = new ParentContactCard(
                    contact, 1, createLogic(), () -> { }, "");

            ImageView photo = getField(card, "photo");
            Label placeholder = getField(card, "photoPlaceholder");

            assertNotNull(placeholder);
            assertFalse(photo.isVisible());
            assertFalse(photo.isManaged());
            assertTrue(placeholder.isVisible());
            assertTrue(placeholder.isManaged());

            return null;
        });
    }

    @Test
    void constructorWithInvalidPhotoPath_showsPlaceholder()
            throws Exception {
        runOnFxThread(() -> {
            ParentContact contact = new ParentContactBuilder()
                    .withPhotoPath("invalid\u0000path.png")
                    .build();

            ParentContactCard card = new ParentContactCard(
                    contact, 1, createLogic(), () -> { }, "");

            ImageView photo = getField(card, "photo");
            Label placeholder = getField(card, "photoPlaceholder");

            assertFalse(photo.isVisible());
            assertFalse(photo.isManaged());
            assertTrue(placeholder.isVisible());
            assertTrue(placeholder.isManaged());

            return null;
        });
    }

    @Test
    void constructorWithKeyword_highlightsMatchingName()
            throws Exception {
        runOnFxThread(() -> {
            ParentContact contact = new ParentContactBuilder()
                    .withName("Alice Tan")
                    .build();

            ParentContactCard card = new ParentContactCard(
                    contact, 1, createLogic(), () -> { }, "Alice");

            TextFlow name = getField(card, "name");

            assertEquals("Alice Tan", getDisplayedText(name));

            assertTrue(name.getChildren().stream()
                    .filter(Text.class::isInstance)
                    .map(Text.class::cast)
                    .anyMatch(text -> text.getText().equals("Alice")
                            && text.getStyleClass()
                            .contains("search-match")));

            assertTrue(name.getChildren().stream()
                    .filter(Text.class::isInstance)
                    .map(Text.class::cast)
                    .anyMatch(text -> text.getText().equals(" Tan")
                            && text.getStyleClass()
                            .contains("normal-card-text")));

            return null;
        });
    }

    @Test
    void constructorWithEmptyKeyword_doesNotHighlightName()
            throws Exception {
        runOnFxThread(() -> {
            ParentContact contact = new ParentContactBuilder()
                    .withName("Alice Tan")
                    .build();

            ParentContactCard card = new ParentContactCard(
                    contact, 1, createLogic(), () -> { }, "");

            TextFlow name = getField(card, "name");

            assertEquals("Alice Tan", getDisplayedText(name));

            assertTrue(name.getChildren().stream()
                    .filter(Text.class::isInstance)
                    .map(Text.class::cast)
                    .noneMatch(text -> text.getStyleClass()
                            .contains("search-match")));

            return null;
        });
    }

    @Test
    void constructorWithNullKeyword_doesNotHighlightName()
            throws Exception {
        runOnFxThread(() -> {
            ParentContact contact = new ParentContactBuilder()
                    .withName("Alice Tan")
                    .build();

            ParentContactCard card = new ParentContactCard(
                    contact, 1, createLogic(), () -> { }, null);

            TextFlow name = getField(card, "name");

            assertEquals("Alice Tan", getDisplayedText(name));

            assertTrue(name.getChildren().stream()
                    .filter(Text.class::isInstance)
                    .map(Text.class::cast)
                    .noneMatch(text -> text.getStyleClass()
                            .contains("search-match")));

            return null;
        });
    }
}
