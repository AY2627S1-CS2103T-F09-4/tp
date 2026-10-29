package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.model.Model;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.reminder.ReminderDisplay;

/** Lists all tutor-level reminders in insertion order. */
public class ListRemindersCommand extends Command {
    public static final String COMMAND_WORD = "list-reminders";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all reminders.\n"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_NO_REMINDERS = "No reminders have been set.";
    public static final String MESSAGE_HEADER = "Reminders:";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Reminder> reminders = ReminderDisplay.sorted(model.getReminderList(), model.getClock());
        if (reminders.isEmpty()) {
            return new CommandResult(MESSAGE_NO_REMINDERS);
        }
        StringBuilder result = new StringBuilder(MESSAGE_HEADER);
        for (Reminder reminder : reminders) {
            result.append(System.lineSeparator()).append(ReminderDisplay.format(reminder, model.getClock()));
        }
        return new CommandResult(result.toString());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ListRemindersCommand;
    }
}
