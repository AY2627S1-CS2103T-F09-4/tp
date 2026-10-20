package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMINDER;

import java.util.regex.Pattern;

import seedu.address.logic.commands.AddReminderCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.reminder.Reminder;

/** Parses arguments for {@link AddReminderCommand}. */
public class AddReminderCommandParser implements Parser<AddReminderCommand> {
    private static final Pattern UNEXPECTED_PREFIX = Pattern.compile(
            "(^|\\s)(n|p|e|a|s|g|f|t|task|tid|status|link|day|from|to)/");

    @Override
    public AddReminderCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_REMINDER);
        if (!argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_REMINDER).isEmpty()) {
            throw invalidFormat();
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_REMINDER);
        String text = argMultimap.getValue(PREFIX_REMINDER).orElse("");
        if (UNEXPECTED_PREFIX.matcher(text).find()) {
            throw invalidFormat();
        }
        try {
            Reminder reminder = ParserUtil.parseReminder(text);
            return new AddReminderCommand(reminder);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddReminderCommand.MESSAGE_USAGE), pe);
        }
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddReminderCommand.MESSAGE_USAGE));
    }
}
