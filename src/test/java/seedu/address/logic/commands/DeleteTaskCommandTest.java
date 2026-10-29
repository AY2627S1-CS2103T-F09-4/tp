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
import java.util.List;

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
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskStatus;
import seedu.address.testutil.PersonBuilder;

public class DeleteTaskCommandTest {

    private static final Index TASK_ONE = Index.fromOneBased(1);
    private static final Index TASK_TWO = Index.fromOneBased(2);
    private static final Index TASK_THREE = Index.fromOneBased(3);

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullIndexes_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteTaskCommand(null, TASK_ONE));
        assertThrows(NullPointerException.class, () -> new DeleteTaskCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_middleTask_removesOnlyThatTaskAndShiftsOthers() {
        Person before = setFirstDisplayedPersonTasks(new Task("a"), new Task("b", TaskStatus.DONE), new Task("c"));
        Person after = withTasks(before, new Task("a"), new Task("c"));
        DeleteTaskCommand command = new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_TWO);

        assertCommandSuccess(command, model, deletedMessage(2, "b", after), expectedModel(before, after));
        assertEquals(List.of(new Task("a"), new Task("c")),
                model.getFilteredPersonList().get(0).getTaskList().getTasks());
    }

    @Test
    public void execute_firstTask_success() {
        Person before = setFirstDisplayedPersonTasks(new Task("a"), new Task("b"));
        Person after = withTasks(before, new Task("b"));
        DeleteTaskCommand command = new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_ONE);

        assertCommandSuccess(command, model, deletedMessage(1, "a", after), expectedModel(before, after));
    }

    @Test
    public void execute_onlyTask_leavesEmptyTaskList() {
        Person before = setFirstDisplayedPersonTasks(new Task("a"));
        Person after = withTasks(before);
        DeleteTaskCommand command = new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_ONE);

        assertCommandSuccess(command, model, deletedMessage(1, "a", after), expectedModel(before, after));
        assertEquals(0, model.getFilteredPersonList().get(0).getTaskList().size());
    }

    @Test
    public void execute_filteredList_usesDisplayedPersonIndex() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person before = setFirstDisplayedPersonTasks(new Task("a"), new Task("b"));
        Person after = withTasks(before, new Task("a"));
        DeleteTaskCommand command = new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_TWO);

        assertCommandSuccess(command, model, deletedMessage(2, "b", after), expectedModel(before, after));
    }

    @Test
    public void execute_studentWithScheduledSession_sessionStillBelongsToStudent() throws Exception {
        Person before = setFirstDisplayedPersonTasks(new Task("a"));
        model.addScheduledSession(new ScheduledSession(before, DayOfWeek.MONDAY, TimeSlot.of("10-00", "12-00")));

        new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_ONE).execute(model);

        Person after = model.getFilteredPersonList().get(0);
        assertEquals(1, model.getScheduleList().size());
        assertEquals(after, model.getScheduleList().get(0).getStudents().getFirst());
    }

    @Test
    public void execute_invalidPersonIndex_failure() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteTaskCommand command = new DeleteTaskCommand(outOfBounds, TASK_ONE);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_taskIndexBeyondList_failureAndTasksUnchanged() {
        Person before = setFirstDisplayedPersonTasks(new Task("a"), new Task("b"));
        DeleteTaskCommand command = new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_THREE);

        assertCommandFailure(command, model, DeleteTaskCommand.MESSAGE_INVALID_TASK_INDEX);
        assertEquals(before, model.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_personWithoutTasks_failure() {
        setFirstDisplayedPersonTasks();
        DeleteTaskCommand command = new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_ONE);

        assertCommandFailure(command, model, DeleteTaskCommand.MESSAGE_INVALID_TASK_INDEX);
    }

    @Test
    public void equals() {
        DeleteTaskCommand first = new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_ONE);
        DeleteTaskCommand same = new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_ONE);

        assertTrue(first.equals(first));
        assertTrue(first.equals(same));
        assertFalse(first.equals(new DeleteTaskCommand(INDEX_SECOND_PERSON, TASK_ONE)));
        assertFalse(first.equals(new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_TWO)));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new ClearCommand()));
    }

    @Test
    public void toStringMethod() {
        DeleteTaskCommand command = new DeleteTaskCommand(INDEX_FIRST_PERSON, TASK_TWO);
        String expected = DeleteTaskCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", taskIndex=" + TASK_TWO + "}";

        assertEquals(expected, command.toString());
    }

    private static Person withTasks(Person base, Task... tasks) {
        PersonBuilder builder = new PersonBuilder(base).withTasks();
        for (Task task : tasks) {
            builder.withTask(task.description, task.status);
        }
        return builder.build();
    }

    private Person setFirstDisplayedPersonTasks(Task... tasks) {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person updated = withTasks(original, tasks);
        model.setPerson(original, updated);
        return updated;
    }

    private Model expectedModel(Person before, Person after) {
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expected.setPerson(before, after);
        return expected;
    }

    private static String deletedMessage(int taskId, String description, Person updated) {
        return String.format(DeleteTaskCommand.MESSAGE_DELETE_TASK_SUCCESS, taskId, description,
                Messages.format(updated));
    }
}
