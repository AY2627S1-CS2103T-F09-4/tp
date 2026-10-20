package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.reminder.Reminder;

/** Adds a tutor-level reminder. */
public class AddReminderCommand extends Command {
    public static final String COMMAND_WORD = "add-reminder";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a reminder.\n"
            + "Parameters: r/REMINDER_TEXT\nExample: " + COMMAND_WORD + " r/Collect worksheets from cabinet B";
    public static final String MESSAGE_SUCCESS = "Added reminder: %1$s";

    private final Reminder reminder;

    public AddReminderCommand(Reminder reminder) {
        this.reminder = requireNonNull(reminder);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.addReminder(reminder);
        return new CommandResult(String.format(MESSAGE_SUCCESS, reminder));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AddReminderCommand otherCommand
                && reminder.equals(otherCommand.reminder);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("reminder", reminder).toString();
    }
}
