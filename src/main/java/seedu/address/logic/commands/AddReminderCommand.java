package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.reminder.ReminderDisplay;

/** Adds a tutor-level reminder. */
public class AddReminderCommand extends Command {
    public static final String COMMAND_WORD = "add-reminder";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a reminder.\n"
            + "Parameters: r/REMINDER_TEXT [at/YYYY-MM-DD HH:mm]\nExample: " + COMMAND_WORD
            + " r/Call Alice's parent at/2026-10-22 18:00";
    public static final String MESSAGE_SUCCESS = "Added reminder: %1$s";

    private final String text;
    private final LocalDateTime dueAt;

    /** Creates an add-reminder command with optional due time. */
    public AddReminderCommand(String text, LocalDateTime dueAt) {
        this.text = requireNonNull(text);
        this.dueAt = dueAt;
    }

    public AddReminderCommand(String text) {
        this(text, null);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        Reminder reminder = model.addReminder(text, dueAt);
        return new CommandResult(String.format(MESSAGE_SUCCESS, ReminderDisplay.format(reminder, model.getClock())));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AddReminderCommand otherCommand
                && text.equals(otherCommand.text) && Objects.equals(dueAt, otherCommand.dueAt);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("text", text).add("dueAt", dueAt).toString();
    }
}
