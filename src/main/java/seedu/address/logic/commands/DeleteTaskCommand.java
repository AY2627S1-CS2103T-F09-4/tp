package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskList;

/**
 * Deletes a task from the task list of a person identified by displayed index.
 */
public class DeleteTaskCommand extends Command {

    public static final String COMMAND_WORD = "delete-task";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a task of the student identified by the index number used in the displayed student list.\n"
            + "Parameters: INDEX tid/TASK_ID\n"
            + "Example: " + COMMAND_WORD + " 1 tid/1";

    public static final String MESSAGE_DELETE_TASK_SUCCESS = "Deleted task %1$d (%2$s) from student: %3$s";
    public static final String MESSAGE_INVALID_TASK_INDEX = "The task index provided is invalid.";

    private final Index targetIndex;
    private final Index taskIndex;

    /**
     * Creates a DeleteTaskCommand to remove a task from a student's task list.
     */
    public DeleteTaskCommand(Index targetIndex, Index taskIndex) {
        requireNonNull(targetIndex);
        requireNonNull(taskIndex);
        this.targetIndex = targetIndex;
        this.taskIndex = taskIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(targetIndex.getZeroBased());
        TaskList taskList = personToUpdate.getTaskList();

        if (taskIndex.getZeroBased() >= taskList.size()) {
            throw new CommandException(MESSAGE_INVALID_TASK_INDEX);
        }

        Task deletedTask = taskList.getTask(taskIndex);
        Person updatedPerson = new Person(personToUpdate.getName(), personToUpdate.getPhone(),
                personToUpdate.getEmail(), personToUpdate.getAddress(), personToUpdate.getSubject(),
                personToUpdate.getGradeLevel(), personToUpdate.getPhotoPath(),
                taskList.withoutTask(taskIndex), personToUpdate.getTags());

        model.setPerson(personToUpdate, updatedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_DELETE_TASK_SUCCESS, taskIndex.getOneBased(),
                deletedTask, Messages.format(updatedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteTaskCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex) && taskIndex.equals(otherCommand.taskIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("taskIndex", taskIndex)
                .toString();
    }
}
