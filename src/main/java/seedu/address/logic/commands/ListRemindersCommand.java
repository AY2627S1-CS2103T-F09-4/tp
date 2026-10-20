package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.model.Model;
import seedu.address.model.reminder.Reminder;

/** Lists all tutor-level reminders in insertion order. */
public class ListRemindersCommand extends Command {
    public static final String COMMAND_WORD = "list-reminders";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all reminders.";
    public static final String MESSAGE_NO_REMINDERS = "No reminders have been set.";
    public static final String MESSAGE_HEADER = "Reminders:";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Reminder> reminders = model.getReminderList();
        if (reminders.isEmpty()) {
            return new CommandResult(MESSAGE_NO_REMINDERS);
        }
        StringBuilder result = new StringBuilder(MESSAGE_HEADER);
        for (int i = 0; i < reminders.size(); i++) {
            result.append(System.lineSeparator()).append(i + 1).append(". ").append(reminders.get(i));
        }
        return new CommandResult(result.toString());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ListRemindersCommand;
    }
}
