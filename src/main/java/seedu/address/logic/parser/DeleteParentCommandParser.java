package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteParentCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class DeleteParentCommandParser implements Parser<DeleteParentCommand> {
    @Override public DeleteParentCommand parse(String args) throws ParseException {
        return new DeleteParentCommand(ParserUtil.parseIndex(args));
    }
}
