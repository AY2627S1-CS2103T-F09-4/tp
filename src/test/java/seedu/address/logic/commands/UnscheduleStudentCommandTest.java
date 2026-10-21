package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalSchedule.ALICE_MONDAY_MORNING;
import static seedu.address.testutil.TypicalSchedule.ALICE_WEDNESDAY;
import static seedu.address.testutil.TypicalSchedule.BENSON_MONDAY_OVERLAP;
import static seedu.address.testutil.TypicalSchedule.getTypicalAddressBookWithSchedule;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;

public class UnscheduleStudentCommandTest {

    private static final TimeSlot MORNING = TimeSlot.of("10-00", "12-00");

    private final Model model = new ModelManager(getTypicalAddressBookWithSchedule(), new UserPrefs());

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new UnscheduleStudentCommand(null, DayOfWeek.MONDAY, MORNING));
        assertThrows(NullPointerException.class, () ->
                new UnscheduleStudentCommand(INDEX_FIRST_PERSON, null, MORNING));
        assertThrows(NullPointerException.class, () ->
                new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, null));
    }

    @Test
    public void execute_existingSession_removesOnlyThatSession() {
        UnscheduleStudentCommand command = new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.deleteScheduledSession(ALICE_MONDAY_MORNING);
        String expectedMessage = "Unscheduled " + ALICE.getName() + " on Monday from 10-00 to 12-00";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(ALICE_WEDNESDAY, BENSON_MONDAY_OVERLAP), model.getScheduleList());
    }

    @Test
    public void execute_duplicateSessions_removesExactlyOne() throws Exception {
        model.addScheduledSession(new ScheduledSession(ALICE, DayOfWeek.MONDAY, MORNING));
        UnscheduleStudentCommand command = new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING);

        command.execute(model);

        long remaining = model.getScheduleList().stream().filter(ALICE_MONDAY_MORNING::isSameSlot).count();
        assertEquals(1, remaining);
        assertEquals(3, model.getScheduleList().size());
    }

    @Test
    public void execute_overlappingSessionOfOtherStudent_unaffected() throws Exception {
        new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING).execute(model);

        assertTrue(model.getScheduleList().contains(BENSON_MONDAY_OVERLAP));
    }

    @Test
    public void execute_filteredList_usesDisplayedIndex() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        UnscheduleStudentCommand command = new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY,
                TimeSlot.of("11-00", "13-00"));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.deleteScheduledSession(BENSON_MONDAY_OVERLAP);
        String expectedMessage = "Unscheduled " + BENSON.getName() + " on Monday from 11-00 to 13-00";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sessionNotScheduled_failureAndScheduleUnchanged() {
        List<ScheduledSession> before = List.copyOf(model.getScheduleList());

        // different day
        assertNotScheduled(DayOfWeek.TUESDAY, "10-00", "12-00", "Tuesday");
        // different start, different end, and a merely overlapping slot
        assertNotScheduled(DayOfWeek.MONDAY, "10-30", "12-00", "Monday");
        assertNotScheduled(DayOfWeek.MONDAY, "10-00", "11-00", "Monday");
        assertNotScheduled(DayOfWeek.MONDAY, "09-00", "13-00", "Monday");

        assertEquals(before, model.getScheduleList());
    }

    @Test
    public void execute_sessionBelongsToOtherStudent_failure() {
        // 11-00 to 13-00 on Monday is scheduled for Benson, not Alice
        UnscheduleStudentCommand command = new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY,
                TimeSlot.of("11-00", "13-00"));

        assertCommandFailure(command, model, String.format(UnscheduleStudentCommand.MESSAGE_SESSION_NOT_FOUND,
                ALICE.getName(), "Monday", "11-00", "13-00"));
    }

    @Test
    public void execute_sessionAlreadyRemoved_failure() throws Exception {
        UnscheduleStudentCommand command = new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING);
        command.execute(model);

        assertCommandFailure(command, model, String.format(UnscheduleStudentCommand.MESSAGE_SESSION_NOT_FOUND,
                ALICE.getName(), "Monday", "10-00", "12-00"));
    }

    @Test
    public void execute_invalidIndex_failure() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UnscheduleStudentCommand command = new UnscheduleStudentCommand(outOfBoundsIndex, DayOfWeek.MONDAY, MORNING);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        UnscheduleStudentCommand first = new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING);
        UnscheduleStudentCommand same = new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY,
                TimeSlot.of("10-00", "12-00"));

        assertTrue(first.equals(first));
        assertTrue(first.equals(same));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new ClearCommand()));
        assertFalse(first.equals(new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING)));
        assertFalse(first.equals(new UnscheduleStudentCommand(INDEX_SECOND_PERSON, DayOfWeek.MONDAY, MORNING)));
        assertFalse(first.equals(new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.TUESDAY, MORNING)));
        assertFalse(first.equals(new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY,
                TimeSlot.of("10-00", "13-00"))));
    }

    @Test
    public void toStringMethod() {
        UnscheduleStudentCommand command = new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING);
        String expected = UnscheduleStudentCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", day=MONDAY, timeSlot=10-00 to 12-00}";

        assertEquals(expected, command.toString());
    }

    private void assertNotScheduled(DayOfWeek day, String from, String to, String dayName) {
        UnscheduleStudentCommand command = new UnscheduleStudentCommand(INDEX_FIRST_PERSON, day,
                TimeSlot.of(from, to));
        assertCommandFailure(command, model, String.format(UnscheduleStudentCommand.MESSAGE_SESSION_NOT_FOUND,
                ALICE.getName(), dayName, from, to));
    }
}
