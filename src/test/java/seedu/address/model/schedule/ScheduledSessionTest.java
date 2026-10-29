package seedu.address.model.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.ScheduledSessionBuilder;

public class ScheduledSessionTest {

    private static final TimeSlot SLOT = TimeSlot.of("10-00", "12-00");

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ScheduledSession((Person) null, DayOfWeek.MONDAY, SLOT));
        assertThrows(NullPointerException.class, () ->
                new ScheduledSession((List<Person>) null, DayOfWeek.MONDAY, SLOT));
        assertThrows(NullPointerException.class, () -> new ScheduledSession(ALICE, null, SLOT));
        assertThrows(NullPointerException.class, () -> new ScheduledSession(ALICE, DayOfWeek.MONDAY, null));

        List<Person> studentsWithNull = new ArrayList<>();
        studentsWithNull.add(ALICE);
        studentsWithNull.add(null);
        assertThrows(NullPointerException.class, () ->
                new ScheduledSession(studentsWithNull, DayOfWeek.MONDAY, SLOT));
    }

    @Test
    public void constructor_noStudents_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ScheduledSession.MESSAGE_NO_STUDENTS, () ->
                new ScheduledSession(List.of(), DayOfWeek.MONDAY, SLOT));
    }

    @Test
    public void constructor_sameStudentTwice_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ScheduledSession.MESSAGE_DUPLICATE_STUDENTS, () ->
                new ScheduledSession(List.of(ALICE, BENSON, ALICE), DayOfWeek.MONDAY, SLOT));
    }

    @Test
    public void getters() {
        ScheduledSession session = new ScheduledSessionBuilder().withStudent(ALICE).withDay(DayOfWeek.TUESDAY)
                .withTimeSlot("09-00", "10-00").build();
        assertEquals(List.of(ALICE), session.getStudents());
        assertEquals(DayOfWeek.TUESDAY, session.getDay());
        assertEquals(TimeSlot.of("09-00", "10-00"), session.getTimeSlot());
    }

    @Test
    public void getStudents_groupSession_keepsScheduledOrderAndIsUnmodifiable() {
        List<Person> students = new ArrayList<>(List.of(CARL, ALICE, BENSON));
        ScheduledSession session = new ScheduledSession(students, DayOfWeek.MONDAY, SLOT);
        students.clear();

        assertEquals(List.of(CARL, ALICE, BENSON), session.getStudents());
        assertThrows(UnsupportedOperationException.class, () -> session.getStudents().add(BOB));
    }

    @Test
    public void hasStudent() {
        ScheduledSession session = new ScheduledSessionBuilder().withStudents(ALICE, BENSON).build();
        assertTrue(session.hasStudent(ALICE));
        assertTrue(session.hasStudent(BENSON));
        assertFalse(session.hasStudent(CARL));
    }

    @Test
    public void isAt() {
        ScheduledSession session = new ScheduledSessionBuilder().withDay(DayOfWeek.MONDAY).build();
        assertTrue(session.isAt(DayOfWeek.MONDAY, SLOT));
        assertFalse(session.isAt(DayOfWeek.TUESDAY, SLOT));
        assertFalse(session.isAt(DayOfWeek.MONDAY, TimeSlot.of("10-00", "11-00")));
    }

    @Test
    public void withStudentReplaced_studentInSession_replacesOnlyThatStudentAndKeepsIdentity() {
        ScheduledSession session = new ScheduledSessionBuilder().withStudents(ALICE, BENSON).build();
        ScheduledSession replaced = session.withStudentReplaced(ALICE, BOB);

        assertEquals(List.of(BOB, BENSON), replaced.getStudents());
        assertEquals(session.getSessionId(), replaced.getSessionId());
        assertEquals(session.getDay(), replaced.getDay());
        assertEquals(session.getTimeSlot(), replaced.getTimeSlot());
        assertEquals(List.of(ALICE, BENSON), session.getStudents());
    }

    @Test
    public void withStudentReplaced_studentNotInSession_returnsSameSession() {
        ScheduledSession session = new ScheduledSessionBuilder().withStudent(ALICE).build();
        assertSame(session, session.withStudentReplaced(CARL, BOB));
    }

    @Test
    public void getStudentsWithout() {
        ScheduledSession session = new ScheduledSessionBuilder().withStudents(ALICE, BENSON, CARL).build();
        assertEquals(List.of(ALICE, CARL), session.getStudentsWithout(BENSON));
        assertEquals(List.of(), new ScheduledSessionBuilder().withStudent(ALICE).build().getStudentsWithout(ALICE));
        assertThrows(IllegalArgumentException.class, () -> session.getStudentsWithout(BOB));
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
        assertFalse(session.equals(new ScheduledSessionBuilder(session).withStudents(ALICE, BOB).build()));
        assertFalse(session.equals(new ScheduledSessionBuilder(session).withDay(DayOfWeek.FRIDAY).build()));
        assertFalse(session.equals(new ScheduledSessionBuilder(session).withTimeSlot("10-00", "13-00").build()));
    }

    @Test
    public void toStringMethod() {
        ScheduledSession session = new ScheduledSessionBuilder().withStudent(ALICE).build();
        assertEquals(ALICE.getName() + " on MONDAY 10-00 to 12-00", session.toString());

        ScheduledSession group = new ScheduledSessionBuilder().withStudents(ALICE, BENSON).build();
        assertEquals(ALICE.getName() + ", " + BENSON.getName() + " on MONDAY 10-00 to 12-00", group.toString());
    }
}
