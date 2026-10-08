package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeletePhotoCommand;

public class DeletePhotoCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePhotoCommand.MESSAGE_USAGE);

    private DeletePhotoCommandParser parser = new DeletePhotoCommandParser();

    @Test
    public void parse_validArgs_returnsDeletePhotoCommand() {
        assertParseSuccess(parser, "1", new DeletePhotoCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_failure() {
        assertParseFailure(parser, "0", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "a", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }
}
