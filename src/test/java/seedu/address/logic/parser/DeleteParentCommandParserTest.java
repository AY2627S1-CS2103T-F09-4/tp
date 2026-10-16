package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteParentCommand;

public class DeleteParentCommandParserTest {
    private final DeleteParentCommandParser parser = new DeleteParentCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteParentCommand() throws Exception {
        assertTrue(parser.parse("1") instanceof DeleteParentCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteParentCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "a", expected);
        assertParseFailure(parser, "", expected);
        assertParseFailure(parser, "0", expected);
    }
}
