package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListRemindersCommand;

class ListRemindersCommandParserTest {
    private final ListRemindersCommandParser parser = new ListRemindersCommandParser();

    @Test void parse_noArguments_success() {
        assertParseSuccess(parser, "  ", new ListRemindersCommand());
    }

    @Test void parse_argumentsOrPrefix_failure() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListRemindersCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "1", message);
        assertParseFailure(parser, " r/extra", message);
    }
}
