package seedu.address.model.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
