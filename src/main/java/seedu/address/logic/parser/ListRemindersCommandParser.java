package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ListRemindersCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the argument-free list-reminders command. */
public class ListRemindersCommandParser implements Parser<ListRemindersCommand> {
    @Override
    public ListRemindersCommand parse(String args) throws ParseException {
        if (!args.trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListRemindersCommand.MESSAGE_USAGE));
        }
        return new ListRemindersCommand();
    }
}
