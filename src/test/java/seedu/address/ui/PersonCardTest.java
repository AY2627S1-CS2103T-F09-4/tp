
package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import seedu.address.model.person.Person;
import seedu.address.model.task.TaskStatus;
import seedu.address.testutil.PersonBuilder;

class PersonCardTest {

    @BeforeAll static void initialiseJavaFx() throws Exception {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
            // The JavaFX toolkit has already been initialized.
        }
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX toolkit did not initialise in time");
    }

    private static <T> T runOnFxThread(Callable<T> operation) throws Exception {
        FutureTask<T> task = new FutureTask<>(operation);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }

    private static <T> T getField(PersonCard card, String fieldName) throws ReflectiveOperationException {
        Field field = PersonCard.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(card);
    }

    private static String getDisplayedText(Node node) {
        if (node instanceof Text) {
            return ((Text) node).getText();
        }
        if (node instanceof TextFlow) {
            TextFlow textflow = (TextFlow) node;
            StringBuilder result = new StringBuilder();

            for (Node child : textflow.getChildren()) {
                result.append(getDisplayedText(child));
            }

            return result.toString();
        }

        return "";
    }

    @Test void constructor_displaysPersonAttributes() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder().withName("Alice Tan").withPhone("91234567")
                    .withEmail("alice@example.com").withAddress("123 Example Street").withSubject("Science").build();

            PersonCard card = new PersonCard(person, 1, null);
            Label id = getField(card, "id");
            assertEquals("1. ", id.getText());

            assertEquals("Alice Tan", getDisplayedText(getField(card, "name")));
            assertEquals("91234567", getDisplayedText(getField(card, "phone")));
            assertEquals("alice@example.com", getDisplayedText(getField(card, "email")));
            assertEquals("123 Example Street", getDisplayedText(getField(card, "address")));
            assertEquals("Science", getDisplayedText(getField(card, "subject")));
            return null;
        });
    }

    @Test void constructorWithoutGradeLevelHidesGradeLevel() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder().build();
            PersonCard card = new PersonCard(person, 1, "");
            TextFlow gradeLevel = getField(card, "gradeLevel");
            assertFalse(gradeLevel.isVisible());
            assertFalse(gradeLevel.isManaged());
            return null;
        });
    }

    @Test void constructorWithGradeLevelDisplaysGradeLevel() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder().withGradeLevel("10").build();

            PersonCard card = new PersonCard(person, 1, "");

            TextFlow gradeLevel = getField(card, "gradeLevel");

            assertTrue(gradeLevel.isVisible());
            assertTrue(gradeLevel.isManaged());
            assertEquals("10", getDisplayedText(gradeLevel));

            return null;
        });
    }

    @Test void constructor_sortsTagsAlphabetically() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder().withTags("friends", "colleagues", "family").build();

            PersonCard card = new PersonCard(person, 1, "");
            FlowPane tags = getField(card, "tags");

            assertEquals(3, tags.getChildren().size());
            assertEquals("colleagues", getDisplayedText(tags.getChildren().get(0)));
            assertEquals("family", getDisplayedText(tags.getChildren().get(1)));
            assertEquals("friends", getDisplayedText(tags.getChildren().get(2)));

            return null;
        });
    }

    @Test void constructor_displaysTasksWithTaskItemStyle() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder().withTasks("Call Alice", "Prepare notes").build();

            PersonCard card = new PersonCard(person, 1, "");
            VBox tasks = getField(card, "tasks");

            assertEquals(2, tasks.getChildren().size());

            HBox firstRow = (HBox) tasks.getChildren().get(0);
            HBox secondRow = (HBox) tasks.getChildren().get(1);
            Node firstTask = firstRow.getChildren().get(1);
            Node secondTask = secondRow.getChildren().get(1);

            assertTrue(firstTask.getStyleClass().contains("task-item"));
            assertTrue(secondTask.getStyleClass().contains("task-item"));

            assertEquals("1.", ((Label) firstRow.getChildren().get(0)).getText());
            assertEquals("2.", ((Label) secondRow.getChildren().get(0)).getText());
            assertEquals("Call Alice", getDisplayedText(firstTask));
            assertEquals("Prepare notes", getDisplayedText(secondTask));

            return null;
        });
    }

    @Test void constructor_styleTasksByStatus() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder().withTasks()
                    .withTask("Finished task", TaskStatus.DONE)
                    .withTask("Open task", TaskStatus.NOT_DONE).build();

            PersonCard card = new PersonCard(person, 1, "");
            VBox tasks = getField(card, "tasks");

            Node doneTask = ((HBox) tasks.getChildren().get(0)).getChildren().get(1);
            Node notDoneTask = ((HBox) tasks.getChildren().get(1)).getChildren().get(1);

            assertTrue(doneTask.getStyleClass().contains("task-done"));
            assertFalse(doneTask.getStyleClass().contains("task-not-done"));
            assertTrue(notDoneTask.getStyleClass().contains("task-not-done"));
            assertFalse(notDoneTask.getStyleClass().contains("task-done"));

            return null;
        });
    }

    @Test void constructorWithoutPhotoShowsPlaceholder() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder().build();
            PersonCard card = new PersonCard(person, 1, "");

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

    @Test void constructorWithKeywordHighlightsMatchingText() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder().withName("Alice Tan").build();

            PersonCard card = new PersonCard(person, 1, "Alice");
            TextFlow name = getField(card, "name");

            assertEquals("Alice Tan", getDisplayedText(name));

            assertTrue(name.getChildren().stream().filter(Text.class::isInstance).map(Text.class::cast)
                    .anyMatch(text -> text.getText().equals("Alice") && text.getStyleClass()
                            .contains("search-match")));

            assertTrue(name.getChildren().stream().filter(Text.class::isInstance).map(Text.class::cast)
                    .anyMatch(text -> text.getText().equals(" Tan") && text.getStyleClass()
                            .contains("normal-card-text")));

            return null;
        });
    }

    @Test void constructorWithEmptyKeywordDoesNotHighlightText() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder().withName("Alice Tan").build();

            PersonCard card = new PersonCard(person, 1, "");
            TextFlow name = getField(card, "name");

            assertEquals("Alice Tan", getDisplayedText(name));

            assertTrue(name.getChildren().stream().filter(Text.class::isInstance).map(Text.class::cast)
                    .noneMatch(text -> text.getStyleClass().contains("search-match")));

            return null;
        });
    }

    @Test void constructor_setsTaskScrollPositionToBottom() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder().build();
            PersonCard card = new PersonCard(person, 1, "");

            ScrollPane taskScrollPane = getField(card, "taskScrollPane");

            assertEquals(1.0, taskScrollPane.getVvalue(), 0.0001);

            return null;
        });
    }
}
