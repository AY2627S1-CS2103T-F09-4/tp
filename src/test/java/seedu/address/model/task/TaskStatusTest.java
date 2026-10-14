package seedu.address.model.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskStatusTest {

    @Test
    public void isValidStatus_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> TaskStatus.isValidStatus(null));
    }

    @Test
    public void isValidStatus() {
        // invalid
        assertFalse(TaskStatus.isValidStatus(""));
        assertFalse(TaskStatus.isValidStatus(" "));
        assertFalse(TaskStatus.isValidStatus("finished"));
        assertFalse(TaskStatus.isValidStatus("notdone"));
        assertFalse(TaskStatus.isValidStatus("not  done"));
        assertFalse(TaskStatus.isValidStatus("done!"));

        // valid, ignoring case and surrounding whitespace
        assertTrue(TaskStatus.isValidStatus("done"));
        assertTrue(TaskStatus.isValidStatus("not done"));
        assertTrue(TaskStatus.isValidStatus("DONE"));
        assertTrue(TaskStatus.isValidStatus("Not Done"));
        assertTrue(TaskStatus.isValidStatus("  done  "));
        assertTrue(TaskStatus.isValidStatus("\tnot done\n"));
    }

    @Test
    public void fromString_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> TaskStatus.fromString(null));
    }

    @Test
    public void fromString_invalid_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TaskStatus.fromString("finished"));
        assertThrows(IllegalArgumentException.class, () -> TaskStatus.fromString(""));
    }

    @Test
    public void fromString_valid_returnsMatchingStatus() {
        assertEquals(TaskStatus.DONE, TaskStatus.fromString("done"));
        assertEquals(TaskStatus.DONE, TaskStatus.fromString("  DONE "));
        assertEquals(TaskStatus.NOT_DONE, TaskStatus.fromString("not done"));
        assertEquals(TaskStatus.NOT_DONE, TaskStatus.fromString("Not Done"));
    }

    @Test
    public void toString_returnsDisplayText() {
        assertEquals("done", TaskStatus.DONE.toString());
        assertEquals("not done", TaskStatus.NOT_DONE.toString());
    }
}
