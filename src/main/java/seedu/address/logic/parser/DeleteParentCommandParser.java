package seedu.address.logic.parser;

import seedu.address.logic.commands.DeleteParentCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses input arguments for deleting a parent contact. */
public class DeleteParentCommandParser implements Parser<DeleteParentCommand> {
    @Override
    public DeleteParentCommand parse(String args) throws ParseException {
        return new DeleteParentCommand(ParserUtil.parseIndex(args));
    }
}
