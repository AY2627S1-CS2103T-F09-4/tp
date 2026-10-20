package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.reminder.Reminder;

/** Deletes the reminder with a stable ID. */
public class DeleteReminderCommand extends Command {
    public static final String COMMAND_WORD = "delete-reminder";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a reminder.\n"
            + "Parameters: ID (must be a positive integer)\nExample: " + COMMAND_WORD + " 2";
    public static final String MESSAGE_SUCCESS = "Deleted reminder: %1$s";
    public static final String MESSAGE_INVALID_ID = "No reminder has the specified ID.";

    private final int targetId;

    public DeleteReminderCommand(int targetId) {
        this.targetId = targetId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Reminder reminder = model.getReminderList().stream().filter(item -> item.getId() == targetId).findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_INVALID_ID));
        model.deleteReminder(targetId);
        return new CommandResult(String.format(MESSAGE_SUCCESS, reminder));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof DeleteReminderCommand otherCommand
                && targetId == otherCommand.targetId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetId", targetId).toString();
    }
}
