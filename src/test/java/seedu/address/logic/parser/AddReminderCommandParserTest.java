package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddReminderCommand;
import seedu.address.model.reminder.Reminder;

class AddReminderCommandParserTest {
    private final AddReminderCommandParser parser = new AddReminderCommandParser();
    private final String invalidFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            AddReminderCommand.MESSAGE_USAGE);

    @Test void parse_validText_success() {
        assertParseSuccess(parser, " r/  Collect worksheets from cabinet B  ",
                new AddReminderCommand(new Reminder("Collect worksheets from cabinet B")));
        assertParseSuccess(parser, " r/Store worksheets in B/2",
                new AddReminderCommand(new Reminder("Store worksheets in B/2")));
    }

    @Test void parse_missingBlankOrWhitespaceText_failure() {
        assertParseFailure(parser, "", invalidFormat);
        assertParseFailure(parser, "Collect worksheets", invalidFormat);
        assertParseFailure(parser, " r/", invalidFormat);
        assertParseFailure(parser, " r/   ", invalidFormat);
    }

    @Test void parse_duplicateOrUnexpectedPrefix_failure() {
        assertParseFailure(parser, " r/First r/Second",
                "Multiple values specified for the following single-valued field(s): r/");
        assertParseFailure(parser, " n/Alice r/First", invalidFormat);
        assertParseFailure(parser, " r/First n/Alice", invalidFormat);
    }
}
