package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskList;

/**
 * Adds a task to the task list of a person identified by displayed index.
 */
public class AddTaskCommand extends Command {

    public static final String COMMAND_WORD = "add-task";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a task to the student identified by the index number used in the displayed student list.\n"
            + "Parameters: INDEX task/TASK_DESCRIPTION "
            + "(at most " + Task.MAX_DESCRIPTION_LENGTH + " characters)\n"
            + "A student can have at most " + TaskList.MAX_TASKS + " tasks.\n"
            + "Example: " + COMMAND_WORD + " 1 task/do homework";

    public static final String MESSAGE_ADD_TASK_SUCCESS = "Added task to student: %1$s";
    public static final String MESSAGE_TOO_MANY_TASKS = "This student already has " + TaskList.MAX_TASKS
            + " tasks, the most allowed. Remove one with " + DeleteTaskCommand.COMMAND_WORD + " first.";

    private static final Logger logger = LogsCenter.getLogger(AddTaskCommand.class);

    private final Index targetIndex;
    private final Task task;

    /**
     * Creates an AddTaskCommand for the specified person and task.
     */
    public AddTaskCommand(Index targetIndex, Task task) {
        requireNonNull(targetIndex);
        requireNonNull(task);
        this.targetIndex = targetIndex;
        this.task = task;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(targetIndex.getZeroBased());
        if (personToUpdate.getTaskList().isFull()) {
            logger.info("Rejected add-task: " + personToUpdate.getName()
                    + " already has the most tasks allowed");
            throw new CommandException(MESSAGE_TOO_MANY_TASKS);
        }
        assert personToUpdate.getTaskList().size() < TaskList.MAX_TASKS
                : "A full task list was about to grow";

        Person updatedPerson = new Person(personToUpdate.getName(), personToUpdate.getPhone(),
                personToUpdate.getEmail(), personToUpdate.getAddress(), personToUpdate.getSubject(),
                personToUpdate.getGradeLevel(), personToUpdate.getPhotoPath(),
                personToUpdate.getTaskList().withTask(task), personToUpdate.getTags());

        model.setPerson(personToUpdate, updatedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_ADD_TASK_SUCCESS, Messages.format(updatedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddTaskCommand otherAddTaskCommand)) {
            return false;
        }
        return targetIndex.equals(otherAddTaskCommand.targetIndex) && task.equals(otherAddTaskCommand.task);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("task", task)
                .toString();
    }
}
