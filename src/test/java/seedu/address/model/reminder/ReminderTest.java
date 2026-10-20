package seedu.address.model.reminder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ReminderTest {
    @Test void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Reminder(1, null));
    }

    @Test void constructor_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Reminder(1, ""));
        assertThrows(IllegalArgumentException.class, () -> new Reminder(1, "  \t "));
        assertThrows(IllegalArgumentException.class, () -> new Reminder(0, "Valid"));
        assertThrows(IllegalArgumentException.class, () -> new Reminder(-1, "Valid"));
        assertThrows(IllegalArgumentException.class, () -> new Reminder(1, "Valid",
                LocalDateTime.of(2026, 10, 22, 18, 0, 1)));
    }

    @Test void constructor_validText_trimsText() {
        Reminder reminder = new Reminder(7, "  Collect worksheets  ", LocalDateTime.of(2026, 10, 22, 18, 0));
        assertEquals(7, reminder.getId());
        assertEquals("Collect worksheets", reminder.getText());
        assertEquals(LocalDateTime.of(2026, 10, 22, 18, 0), reminder.getDueAt().orElseThrow());
        assertTrue(new Reminder(8, "Untimed").getDueAt().isEmpty());
    }

    @Test void isValidText() {
        assertFalse(Reminder.isValidText(null));
        assertFalse(Reminder.isValidText(""));
        assertFalse(Reminder.isValidText("  "));
        assertTrue(Reminder.isValidText("Ask parents"));
        assertTrue(Reminder.isValidId(1));
        assertFalse(Reminder.isValidId(0));
    }

    @Test void equalityHashCodeAndToString() {
        Reminder reminder = new Reminder(1, "Ask parents", LocalDateTime.of(2026, 10, 22, 18, 0));
        Reminder same = reminder.copy();
        Reminder different = new Reminder(2, "Ask parents", LocalDateTime.of(2026, 10, 22, 18, 0));
        assertEquals(reminder, reminder);
        assertEquals(reminder, same);
        assertEquals(reminder.hashCode(), same.hashCode());
        assertNotEquals(reminder, different);
        assertNotEquals(reminder, null);
        assertNotEquals(reminder, "Ask parents");
        assertEquals("Ask parents", reminder.toString());
    }
}
