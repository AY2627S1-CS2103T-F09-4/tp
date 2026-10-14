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
import seedu.address.model.task.TaskList;
import seedu.address.model.task.TaskStatus;

/**
 * Marks a task in the task list of a person identified by displayed index as done or not done.
 */
public class MarkTaskCommand extends Command {

    public static final String COMMAND_WORD = "mark-task";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks a task of the student identified by the index number used in the displayed student list "
            + "as done or not done.\n"
            + "Parameters: INDEX tid/TASK_ID status/(done|not done)\n"
            + "Example1: " + COMMAND_WORD + " 1 tid/1 status/done\n"
            + "Example2: " + COMMAND_WORD + " 1 tid/1 status/not done";

    public static final String MESSAGE_MARK_TASK_SUCCESS = "Marked task %1$d as %2$s for student: %3$s";
    public static final String MESSAGE_TASK_ALREADY_MARKED = "Task %1$d is already %2$s for student: %3$s";
    public static final String MESSAGE_INVALID_TASK_INDEX = "The task index provided is invalid.";

    private final Index targetIndex;
    private final Index taskIndex;
    private final TaskStatus status;

    /**
     * Creates a MarkTaskCommand to set the status of a student's task.
     */
    public MarkTaskCommand(Index targetIndex, Index taskIndex, TaskStatus status) {
        requireNonNull(targetIndex);
        requireNonNull(taskIndex);
        requireNonNull(status);
        this.targetIndex = targetIndex;
        this.taskIndex = taskIndex;
        this.status = status;
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

        if (taskList.getTask(taskIndex).status == status) {
            return new CommandResult(String.format(MESSAGE_TASK_ALREADY_MARKED, taskIndex.getOneBased(), status,
                    Messages.format(personToUpdate)));
        }

        Person updatedPerson = new Person(personToUpdate.getName(), personToUpdate.getPhone(),
                personToUpdate.getEmail(), personToUpdate.getAddress(), personToUpdate.getSubject(),
                personToUpdate.getGradeLevel(), personToUpdate.getPhotoPath(),
                taskList.withTaskStatus(taskIndex, status), personToUpdate.getTags());

        model.setPerson(personToUpdate, updatedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_MARK_TASK_SUCCESS, taskIndex.getOneBased(), status,
                Messages.format(updatedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MarkTaskCommand otherMarkTaskCommand)) {
            return false;
        }
        return targetIndex.equals(otherMarkTaskCommand.targetIndex)
                && taskIndex.equals(otherMarkTaskCommand.taskIndex)
                && status == otherMarkTaskCommand.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("taskIndex", taskIndex)
                .add("status", status)
                .toString();
    }
}
