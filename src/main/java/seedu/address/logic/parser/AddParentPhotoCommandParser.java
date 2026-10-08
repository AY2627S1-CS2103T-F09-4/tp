package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_FILE_PATH;
import java.nio.file.Path;
import java.nio.file.Paths;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddParentPhotoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class AddParentPhotoCommandParser implements Parser<AddParentPhotoCommand> {
    @Override public AddParentPhotoCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_FILE_PATH);
        if (map.getPreamble().isEmpty() || map.getValue(PREFIX_FILE_PATH).isEmpty()) throw new ParseException(AddParentPhotoCommand.MESSAGE_USAGE);
        Index index = ParserUtil.parseIndex(map.getPreamble());
        Path path = Paths.get(map.getValue(PREFIX_FILE_PATH).get().trim());
        return new AddParentPhotoCommand(index, path);
    }
}
