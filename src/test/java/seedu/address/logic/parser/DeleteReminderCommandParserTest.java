package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteReminderCommand;

class DeleteReminderCommandParserTest {
    private final DeleteReminderCommandParser parser = new DeleteReminderCommandParser();
    private final String invalidFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            DeleteReminderCommand.MESSAGE_USAGE);

    @Test void parse_validIndex_success() {
        assertParseSuccess(parser, " 1 ", new DeleteReminderCommand(INDEX_FIRST_PERSON));
    }

    @Test void parse_zeroNegativeNonNumericMissingOrUnexpectedPrefix_failure() {
        assertParseFailure(parser, "0", invalidFormat);
        assertParseFailure(parser, "-1", invalidFormat);
        assertParseFailure(parser, "one", invalidFormat);
        assertParseFailure(parser, "", invalidFormat);
        assertParseFailure(parser, "1 r/extra", invalidFormat);
    }
}
