package seedu.address.model.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.time.DayOfWeek;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.ScheduledSessionBuilder;

public class ScheduledSessionTest {

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        TimeSlot slot = TimeSlot.of("10-00", "12-00");
        assertThrows(NullPointerException.class, () -> new ScheduledSession(null, DayOfWeek.MONDAY, slot));
        assertThrows(NullPointerException.class, () -> new ScheduledSession(ALICE, null, slot));
        assertThrows(NullPointerException.class, () -> new ScheduledSession(ALICE, DayOfWeek.MONDAY, null));
    }

    @Test
    public void getters() {
        ScheduledSession session = new ScheduledSessionBuilder().withStudent(ALICE).withDay(DayOfWeek.TUESDAY)
                .withTimeSlot("09-00", "10-00").build();
        assertEquals(ALICE, session.getStudent());
        assertEquals(DayOfWeek.TUESDAY, session.getDay());
        assertEquals(TimeSlot.of("09-00", "10-00"), session.getTimeSlot());
    }

    @Test
    public void withStudent_returnsCopyWithNewStudent() {
        ScheduledSession session = new ScheduledSessionBuilder().withStudent(ALICE).build();
        ScheduledSession replaced = session.withStudent(BOB);

        assertEquals(BOB, replaced.getStudent());
        assertEquals(session.getDay(), replaced.getDay());
        assertEquals(session.getTimeSlot(), replaced.getTimeSlot());
        assertEquals(ALICE, session.getStudent());
    }

    @Test
    public void equalsAndHashCode() {
        ScheduledSession session = new ScheduledSessionBuilder().withStudent(ALICE).build();
        ScheduledSession same = new ScheduledSessionBuilder(session).build();

        assertTrue(session.equals(session));
        assertTrue(session.equals(same));
        assertEquals(session.hashCode(), same.hashCode());

        assertFalse(session.equals(null));
        assertFalse(session.equals(5));
        assertFalse(session.equals(new ScheduledSessionBuilder(session).withStudent(BOB).build()));
        assertFalse(session.equals(new ScheduledSessionBuilder(session).withDay(DayOfWeek.FRIDAY).build()));
        assertFalse(session.equals(new ScheduledSessionBuilder(session).withTimeSlot("10-00", "13-00").build()));
    }

    @Test
    public void toStringMethod() {
        ScheduledSession session = new ScheduledSessionBuilder().withStudent(ALICE).build();
        assertEquals(ALICE.getName() + " on MONDAY 10-00 to 12-00", session.toString());
    }
}
