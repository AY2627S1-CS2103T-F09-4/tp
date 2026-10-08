package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FILE_PATH;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddPhotoCommand;

public class AddPhotoCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddPhotoCommand.MESSAGE_USAGE);

    private AddPhotoCommandParser parser = new AddPhotoCommandParser();

    @Test
    public void parse_validArgs_returnsAddPhotoCommand() {
        Path photoPath = Path.of("photos", "amy.png");
        assertParseSuccess(parser, "1 " + PREFIX_FILE_PATH + photoPath,
                new AddPhotoCommand(INDEX_FIRST_PERSON, photoPath));
    }

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, PREFIX_FILE_PATH + "photos/amy.png", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_FILE_PATH, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "0 " + PREFIX_FILE_PATH + "photos/amy.png", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_duplicateFilePathPrefix_failure() {
        String userInput = "1 " + PREFIX_FILE_PATH + "photos/amy.png " + PREFIX_FILE_PATH + "photos/bob.png";
        assertParseFailure(parser, userInput, "Multiple values specified for the following single-valued field(s): f/");
    }
}
