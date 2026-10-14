package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskStatus;
import seedu.address.testutil.PersonBuilder;

public class MarkTaskCommandTest {

    private static final Index TASK_ONE = Index.fromOneBased(1);
    private static final Index TASK_TWO = Index.fromOneBased(2);
    private static final Index TASK_THREE = Index.fromOneBased(3);

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_notDoneToDone_changesOnlyTargetTask() {
        Person before = setFirstDisplayedPersonTasks(new Task("do homework"), new Task("revise notes"));
        Person after = withTasks(before, new Task("do homework"), new Task("revise notes", TaskStatus.DONE));
        MarkTaskCommand command = new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_TWO, TaskStatus.DONE);

        assertCommandSuccess(command, model, markedMessage(2, TaskStatus.DONE, after), expectedModel(before, after));
    }

    @Test
    public void execute_doneToNotDone_success() {
        Person before = setFirstDisplayedPersonTasks(new Task("do homework", TaskStatus.DONE));
        Person after = withTasks(before, new Task("do homework", TaskStatus.NOT_DONE));
        MarkTaskCommand command = new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_ONE, TaskStatus.NOT_DONE);

        assertCommandSuccess(command, model, markedMessage(1, TaskStatus.NOT_DONE, after),
                expectedModel(before, after));
    }

    @Test
    public void execute_alreadyDone_reportsNoChange() {
        Person before = setFirstDisplayedPersonTasks(new Task("do homework", TaskStatus.DONE));
        MarkTaskCommand command = new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_ONE, TaskStatus.DONE);
        String expectedMessage = String.format(MarkTaskCommand.MESSAGE_TASK_ALREADY_MARKED, 1, TaskStatus.DONE,
                Messages.format(before));

        assertCommandSuccess(command, model, expectedMessage, expectedModel(before, before));
    }

    @Test
    public void execute_alreadyNotDone_reportsNoChange() {
        Person before = setFirstDisplayedPersonTasks(new Task("do homework"));
        MarkTaskCommand command = new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_ONE, TaskStatus.NOT_DONE);
        String expectedMessage = String.format(MarkTaskCommand.MESSAGE_TASK_ALREADY_MARKED, 1, TaskStatus.NOT_DONE,
                Messages.format(before));

        assertCommandSuccess(command, model, expectedMessage, expectedModel(before, before));
    }

    @Test
    public void execute_filteredList_usesDisplayedPersonIndex() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person before = setFirstDisplayedPersonTasks(new Task("do homework"));
        Person after = withTasks(before, new Task("do homework", TaskStatus.DONE));
        MarkTaskCommand command = new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_ONE, TaskStatus.DONE);

        assertCommandSuccess(command, model, markedMessage(1, TaskStatus.DONE, after), expectedModel(before, after));
    }

    @Test
    public void execute_invalidPersonIndex_failure() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        MarkTaskCommand command = new MarkTaskCommand(outOfBounds, TASK_ONE, TaskStatus.DONE);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_taskIndexBeyondList_failure() {
        setFirstDisplayedPersonTasks(new Task("do homework"), new Task("revise notes"));
        MarkTaskCommand command = new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_THREE, TaskStatus.DONE);

        assertCommandFailure(command, model, MarkTaskCommand.MESSAGE_INVALID_TASK_INDEX);
    }

    @Test
    public void execute_personWithoutTasks_failure() {
        setFirstDisplayedPersonTasks();
        MarkTaskCommand command = new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_ONE, TaskStatus.DONE);

        assertCommandFailure(command, model, MarkTaskCommand.MESSAGE_INVALID_TASK_INDEX);
    }

    @Test
    public void equals() {
        MarkTaskCommand first = new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_ONE, TaskStatus.DONE);
        MarkTaskCommand same = new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_ONE, TaskStatus.DONE);

        assertTrue(first.equals(first));
        assertTrue(first.equals(same));
        assertFalse(first.equals(new MarkTaskCommand(INDEX_SECOND_PERSON, TASK_ONE, TaskStatus.DONE)));
        assertFalse(first.equals(new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_TWO, TaskStatus.DONE)));
        assertFalse(first.equals(new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_ONE, TaskStatus.NOT_DONE)));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new ClearCommand()));
    }

    @Test
    public void toStringMethod() {
        MarkTaskCommand command = new MarkTaskCommand(INDEX_FIRST_PERSON, TASK_TWO, TaskStatus.DONE);
        String expected = MarkTaskCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", taskIndex=" + TASK_TWO + ", status=done}";

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

    private static String markedMessage(int taskId, TaskStatus status, Person updated) {
        return String.format(MarkTaskCommand.MESSAGE_MARK_TASK_SUCCESS, taskId, status, Messages.format(updated));
    }
}
