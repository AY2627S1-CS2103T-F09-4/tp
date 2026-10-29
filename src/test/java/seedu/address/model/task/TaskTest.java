package seedu.address.model.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void constructor_nullDescription_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Task(null));
    }

    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task(" \t"));
    }

    @Test
    public void isValidDescription() {
        assertFalse(Task.isValidDescription(""));
        assertFalse(Task.isValidDescription(" "));
        assertTrue(Task.isValidDescription("do homework"));
    }

    @Test
    public void isWithinLengthLimit() {
        assertThrows(NullPointerException.class, () -> Task.isWithinLengthLimit(null));
        assertTrue(Task.isWithinLengthLimit("a".repeat(Task.MAX_DESCRIPTION_LENGTH)));
        assertFalse(Task.isWithinLengthLimit("a".repeat(Task.MAX_DESCRIPTION_LENGTH + 1)));
    }

    @Test
    public void constructor_overLengthLimit_stillCreatedSoSavedTasksLoad() {
        String savedDescription = "a".repeat(Task.MAX_DESCRIPTION_LENGTH + 1);
        assertEquals(savedDescription, new Task(savedDescription).description);
    }

    @Test
    public void constructor_nullStatus_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Task("do homework", null));
    }

    @Test
    public void constructor_defaultsToNotDone() {
        assertEquals(TaskStatus.NOT_DONE, new Task("do homework").status);
        assertEquals(TaskStatus.DONE, new Task("do homework", TaskStatus.DONE).status);
    }

    @Test
    public void withStatus_returnsCopyAndLeavesOriginalUnchanged() {
        Task original = new Task("do homework");
        Task done = original.withStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.NOT_DONE, original.status);
        assertEquals(new Task("do homework", TaskStatus.DONE), done);
        assertEquals("do homework", done.description);
    }

    @Test
    public void equals_differentStatus_returnsFalse() {
        Task notDone = new Task("do homework");
        Task done = new Task("do homework", TaskStatus.DONE);

        assertFalse(notDone.equals(done));
        assertFalse(notDone.hashCode() == done.hashCode());
    }

    @Test
    public void copy_doesNotEqualOriginal() {
        Task first = new Task("1", TaskStatus.DONE);
        Task second = first.copy();
        assertNotSame(first, second);
    }

    @Test
    public void equals() {
        Task first = new Task("do homework");
        Task same = new Task("do homework");
        Task different = new Task("revise notes");

        assertEquals(first, first);
        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertFalse(first.equals(different));
        assertFalse(first.equals(null));
        assertFalse(first.equals("do homework"));
    }
}
