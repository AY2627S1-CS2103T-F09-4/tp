package seedu.address.logic.parser;

import seedu.address.logic.commands.DeleteParentPhotoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses input arguments for deleting a parent photo. */
public class DeleteParentPhotoCommandParser implements Parser<DeleteParentPhotoCommand> {
    @Override
    public DeleteParentPhotoCommand parse(String args) throws ParseException {
        return new DeleteParentPhotoCommand(ParserUtil.parseIndex(args));
    }
}
