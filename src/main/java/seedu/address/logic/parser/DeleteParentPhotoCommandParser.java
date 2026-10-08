package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteParentPhotoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class DeleteParentPhotoCommandParser implements Parser<DeleteParentPhotoCommand> {
    @Override public DeleteParentPhotoCommand parse(String args) throws ParseException {
        return new DeleteParentPhotoCommand(ParserUtil.parseIndex(args));
    }
}
