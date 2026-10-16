package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.DeleteParentCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses input arguments for deleting a parent contact. */
public class DeleteParentCommandParser implements Parser<DeleteParentCommand> {
    @Override
    public DeleteParentCommand parse(String args) throws ParseException {
        try {
            return new DeleteParentCommand(ParserUtil.parseIndex(args));
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteParentCommand.MESSAGE_USAGE), pe);
        }
    }
}
