package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.DeleteReminderCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses arguments for {@link DeleteReminderCommand}. */
public class DeleteReminderCommandParser implements Parser<DeleteReminderCommand> {
    @Override
    public DeleteReminderCommand parse(String args) throws ParseException {
        try {
            return new DeleteReminderCommand(ParserUtil.parseIndex(args).getOneBased());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteReminderCommand.MESSAGE_USAGE), pe);
        }
    }
}
