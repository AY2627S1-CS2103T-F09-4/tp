package seedu.address.model.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class TimeSlotTest {

    @Test
    public void constructor_nullTimes_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TimeSlot(null, LocalTime.NOON));
        assertThrows(NullPointerException.class, () -> new TimeSlot(LocalTime.NOON, null));
    }

    @Test
    public void constructor_startNotBeforeEnd_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(LocalTime.NOON, LocalTime.NOON));
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(LocalTime.of(13, 0), LocalTime.NOON));
    }

    @Test
    public void of_invalidInput_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> TimeSlot.of(null, "12-00"));
        assertThrows(NullPointerException.class, () -> TimeSlot.of("10-00", null));
        assertThrows(IllegalArgumentException.class, () -> TimeSlot.of("12-00", "10-00"));
        assertThrows(IllegalArgumentException.class, () -> TimeSlot.of("10:00", "12:00"));
    }

    @Test
    public void of_validInput_success() {
        TimeSlot slot = TimeSlot.of("10-00", "12-30");
        assertEquals(LocalTime.of(10, 0), slot.start);
        assertEquals(LocalTime.of(12, 30), slot.end);
    }

    @Test
    public void isValidTimeSlot() {
        // valid
        assertTrue(TimeSlot.isValidTimeSlot("00-00", "23-59"));
        assertTrue(TimeSlot.isValidTimeSlot("10-00", "10-01"));

        // start not before end
        assertFalse(TimeSlot.isValidTimeSlot("10-00", "10-00"));
        assertFalse(TimeSlot.isValidTimeSlot("12-00", "10-00"));

        // malformed
        assertFalse(TimeSlot.isValidTimeSlot("", "12-00"));
        assertFalse(TimeSlot.isValidTimeSlot("10-00", ""));
        assertFalse(TimeSlot.isValidTimeSlot("10:00", "12:00"));
        assertFalse(TimeSlot.isValidTimeSlot("1000", "1200"));
        assertFalse(TimeSlot.isValidTimeSlot("9-00", "12-00"));
        assertFalse(TimeSlot.isValidTimeSlot("24-00", "25-00"));
        assertFalse(TimeSlot.isValidTimeSlot("10-60", "12-00"));
        assertFalse(TimeSlot.isValidTimeSlot("ab-cd", "12-00"));
    }

    @Test
    public void overlaps() {
        TimeSlot slot = TimeSlot.of("10-00", "12-00");

        // partial overlap, both directions
        assertTrue(slot.overlaps(TimeSlot.of("11-00", "13-00")));
        assertTrue(slot.overlaps(TimeSlot.of("09-00", "11-00")));

        // containment, both directions
        assertTrue(slot.overlaps(TimeSlot.of("10-30", "11-30")));
        assertTrue(slot.overlaps(TimeSlot.of("08-00", "18-00")));

        // identical
        assertTrue(slot.overlaps(TimeSlot.of("10-00", "12-00")));

        // touching at a boundary does not overlap
        assertFalse(slot.overlaps(TimeSlot.of("12-00", "13-00")));
        assertFalse(slot.overlaps(TimeSlot.of("09-00", "10-00")));

        // disjoint
        assertFalse(slot.overlaps(TimeSlot.of("14-00", "15-00")));

        assertThrows(NullPointerException.class, () -> slot.overlaps(null));
    }

    @Test
    public void stringAccessors() {
        TimeSlot slot = TimeSlot.of("09-05", "10-30");
        assertEquals("09-05", slot.getStartString());
        assertEquals("10-30", slot.getEndString());
        assertEquals("09-05 to 10-30", slot.toString());
    }

    @Test
    public void equalsAndHashCode() {
        TimeSlot slot = TimeSlot.of("10-00", "12-00");

        assertTrue(slot.equals(slot));
        assertTrue(slot.equals(TimeSlot.of("10-00", "12-00")));
        assertEquals(slot.hashCode(), TimeSlot.of("10-00", "12-00").hashCode());

        assertFalse(slot.equals(null));
        assertFalse(slot.equals(5));
        assertFalse(slot.equals(TimeSlot.of("10-00", "13-00")));
        assertFalse(slot.equals(TimeSlot.of("09-00", "12-00")));
    }
}
