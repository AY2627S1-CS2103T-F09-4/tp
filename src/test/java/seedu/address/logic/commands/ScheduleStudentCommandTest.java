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
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.DayOfWeek;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;

public class ScheduleStudentCommandTest {

    private static final TimeSlot MORNING = TimeSlot.of("10-00", "12-00");

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ScheduleStudentCommand(null, DayOfWeek.MONDAY, MORNING));
        assertThrows(NullPointerException.class, () -> new ScheduleStudentCommand(INDEX_FIRST_PERSON, null, MORNING));
        assertThrows(NullPointerException.class, () ->
                new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, null));
    }

    @Test
    public void execute_validIndex_addsSession() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ScheduleStudentCommand command = new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.addScheduledSession(new ScheduledSession(student, DayOfWeek.MONDAY, MORNING));
        String expectedMessage = "Scheduled " + student.getName() + " on Monday from 10-00 to 12-00";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(1, model.getScheduleList().size());
    }

    @Test
    public void execute_overlappingSession_bothAdded() throws Exception {
        new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING).execute(model);
        new ScheduleStudentCommand(INDEX_SECOND_PERSON, DayOfWeek.MONDAY, TimeSlot.of("11-00", "13-00"))
                .execute(model);
        new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING).execute(model);

        assertEquals(3, model.getScheduleList().size());
    }

    @Test
    public void execute_filteredList_usesDisplayedIndex() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ScheduleStudentCommand command = new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.FRIDAY, MORNING);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.addScheduledSession(new ScheduledSession(student, DayOfWeek.FRIDAY, MORNING));
        String expectedMessage = "Scheduled " + student.getName() + " on Friday from 10-00 to 12-00";

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_failure() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ScheduleStudentCommand command = new ScheduleStudentCommand(outOfBoundsIndex, DayOfWeek.MONDAY, MORNING);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        assertTrue(model.getScheduleList().isEmpty());
    }

    @Test
    public void equals() {
        ScheduleStudentCommand first = new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING);
        ScheduleStudentCommand same = new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY,
                TimeSlot.of("10-00", "12-00"));

        assertTrue(first.equals(first));
        assertTrue(first.equals(same));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new ClearCommand()));
        assertFalse(first.equals(new ScheduleStudentCommand(INDEX_SECOND_PERSON, DayOfWeek.MONDAY, MORNING)));
        assertFalse(first.equals(new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.TUESDAY, MORNING)));
        assertFalse(first.equals(new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY,
                TimeSlot.of("10-00", "13-00"))));
    }

    @Test
    public void toStringMethod() {
        ScheduleStudentCommand command = new ScheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, MORNING);
        String expected = ScheduleStudentCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", day=MONDAY, timeSlot=10-00 to 12-00}";

        assertEquals(expected, command.toString());
    }
}
