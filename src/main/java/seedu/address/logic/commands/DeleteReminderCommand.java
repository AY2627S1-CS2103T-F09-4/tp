package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.reminder.Reminder;

/** Deletes the reminder at a one-based index. */
public class DeleteReminderCommand extends Command {
    public static final String COMMAND_WORD = "delete-reminder";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a reminder.\n"
            + "Parameters: INDEX (must be a positive integer)\nExample: " + COMMAND_WORD + " 2";
    public static final String MESSAGE_SUCCESS = "Deleted reminder: %1$s";
    public static final String MESSAGE_INVALID_INDEX = "The reminder index provided is invalid.";

    private final Index targetIndex;

    public DeleteReminderCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (targetIndex.getZeroBased() >= model.getReminderList().size()) {
            throw new CommandException(MESSAGE_INVALID_INDEX);
        }
        Reminder reminder = model.getReminderList().get(targetIndex.getZeroBased());
        model.deleteReminder(targetIndex);
        return new CommandResult(String.format(MESSAGE_SUCCESS, reminder));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof DeleteReminderCommand otherCommand
                && targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
