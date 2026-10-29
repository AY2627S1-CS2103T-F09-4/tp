package seedu.address.model.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalSchedule.ALICE_MONDAY_MORNING;
import static seedu.address.testutil.TypicalSchedule.ALICE_WEDNESDAY;
import static seedu.address.testutil.TypicalSchedule.BENSON_MONDAY_OVERLAP;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.ScheduledSessionBuilder;

public class ScheduleTest {

    private final Schedule schedule = new Schedule();

    @Test
    public void constructor_isEmpty() {
        assertEquals(List.of(), schedule.asUnmodifiableObservableList());
    }

    @Test
    public void add_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> schedule.add(null));
    }

    @Test
    public void add_overlappingSessions_bothKept() {
        schedule.add(ALICE_MONDAY_MORNING);
        schedule.add(BENSON_MONDAY_OVERLAP);
        assertEquals(List.of(ALICE_MONDAY_MORNING, BENSON_MONDAY_OVERLAP), schedule.asUnmodifiableObservableList());
    }

    @Test
    public void add_identicalSessionTwice_bothKept() {
        schedule.add(ALICE_MONDAY_MORNING);
        schedule.add(new ScheduledSession(ALICE, ALICE_MONDAY_MORNING.getDay(), ALICE_MONDAY_MORNING.getTimeSlot()));
        assertEquals(2, schedule.asUnmodifiableObservableList().size());
    }

    @Test
    public void containsAndRemove_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> schedule.contains(null));
        assertThrows(NullPointerException.class, () -> schedule.remove(null));
    }

    @Test
    public void contains_matchesByEquality() {
        schedule.add(ALICE_MONDAY_MORNING);

        assertTrue(schedule.contains(ALICE_MONDAY_MORNING));
        assertFalse(schedule.contains(ALICE_WEDNESDAY));
        assertFalse(schedule.contains(BENSON_MONDAY_OVERLAP));
    }

    @Test
    public void remove_existingSession_removesAndReturnsTrue() {
        schedule.add(ALICE_MONDAY_MORNING);
        schedule.add(ALICE_WEDNESDAY);

        assertTrue(schedule.remove(ALICE_MONDAY_MORNING));
        assertEquals(List.of(ALICE_WEDNESDAY), schedule.asUnmodifiableObservableList());
    }

    @Test
    public void remove_absentSession_returnsFalseAndLeavesScheduleUnchanged() {
        schedule.add(ALICE_MONDAY_MORNING);

        assertFalse(schedule.remove(ALICE_WEDNESDAY));
        assertEquals(List.of(ALICE_MONDAY_MORNING), schedule.asUnmodifiableObservableList());
    }

    @Test
    public void remove_duplicateSessions_removesOnlyOne() {
        ScheduledSession duplicate = new ScheduledSession(ALICE, ALICE_MONDAY_MORNING.getDay(),
                ALICE_MONDAY_MORNING.getTimeSlot());
        schedule.add(ALICE_MONDAY_MORNING);
        schedule.add(duplicate);

        assertTrue(schedule.remove(ALICE_MONDAY_MORNING));
        assertEquals(List.of(duplicate), schedule.asUnmodifiableObservableList());
    }

    @Test
    public void removeIf_removesMatchingSessionsOnly() {
        schedule.add(ALICE_MONDAY_MORNING);
        schedule.add(BENSON_MONDAY_OVERLAP);
        schedule.add(ALICE_WEDNESDAY);

        schedule.removeIf(s -> s.getStudents().getFirst().equals(ALICE));

        assertEquals(List.of(BENSON_MONDAY_OVERLAP), schedule.asUnmodifiableObservableList());
        assertThrows(NullPointerException.class, () -> schedule.removeIf(null));
    }

    @Test
    public void setSessions_replacesContents() {
        schedule.add(ALICE_MONDAY_MORNING);
        schedule.setSessions(List.of(BENSON_MONDAY_OVERLAP, ALICE_WEDNESDAY));

        assertEquals(List.of(BENSON_MONDAY_OVERLAP, ALICE_WEDNESDAY), schedule.asUnmodifiableObservableList());
        assertThrows(NullPointerException.class, () -> schedule.setSessions(null));
    }

    @Test
    public void asUnmodifiableObservableList_modify_throwsUnsupportedOperationException() {
        schedule.add(ALICE_MONDAY_MORNING);
        assertThrows(UnsupportedOperationException.class, () -> schedule.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void equalsAndHashCode() {
        Schedule other = new Schedule();
        schedule.add(ALICE_MONDAY_MORNING);
        other.add(ALICE_MONDAY_MORNING);

        assertTrue(schedule.equals(schedule));
        assertTrue(schedule.equals(other));
        assertEquals(schedule.hashCode(), other.hashCode());

        other.add(new ScheduledSessionBuilder().withStudent(BENSON).build());
        assertFalse(schedule.equals(other));
        assertFalse(schedule.equals(null));
        assertFalse(schedule.equals(5));
    }
}
