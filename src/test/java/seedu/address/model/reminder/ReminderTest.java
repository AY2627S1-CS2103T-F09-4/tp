package seedu.address.model.reminder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReminderTest {
    @Test void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Reminder(null));
    }

    @Test void constructor_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Reminder(""));
        assertThrows(IllegalArgumentException.class, () -> new Reminder("  \t "));
    }

    @Test void constructor_validText_trimsText() {
        assertEquals("Collect worksheets", new Reminder("  Collect worksheets  ").text);
    }

    @Test void isValidText() {
        assertFalse(Reminder.isValidText(null));
        assertFalse(Reminder.isValidText(""));
        assertFalse(Reminder.isValidText("  "));
        assertTrue(Reminder.isValidText("Ask parents"));
    }

    @Test void equalityHashCodeAndToString() {
        Reminder reminder = new Reminder("Ask parents");
        Reminder same = new Reminder("Ask parents");
        Reminder different = new Reminder("Collect worksheets");
        assertEquals(reminder, reminder);
        assertEquals(reminder, same);
        assertEquals(reminder.hashCode(), same.hashCode());
        assertNotEquals(reminder, different);
        assertNotEquals(reminder, null);
        assertNotEquals(reminder, "Ask parents");
        assertEquals("Ask parents", reminder.toString());
    }
}
