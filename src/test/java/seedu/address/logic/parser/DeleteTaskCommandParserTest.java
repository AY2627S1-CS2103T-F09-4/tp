package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_ID;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteTaskCommand;

public class DeleteTaskCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTaskCommand.MESSAGE_USAGE);

    private final DeleteTaskCommandParser parser = new DeleteTaskCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteTaskCommand() {
        assertParseSuccess(parser, "1 " + PREFIX_TASK_ID + "1",
                new DeleteTaskCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON));
        assertParseSuccess(parser, " 2 " + PREFIX_TASK_ID + " 1 ",
                new DeleteTaskCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "1 " + PREFIX_TASK_ID + "2",
                new DeleteTaskCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
    }

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, PREFIX_TASK_ID + "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndexes_failure() {
        assertParseFailure(parser, "0 " + PREFIX_TASK_ID + "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "a " + PREFIX_TASK_ID + "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 2 " + PREFIX_TASK_ID + "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "0", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "-1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "a", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_duplicatePrefix_failure() {
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "1 " + PREFIX_TASK_ID + "2",
                "Multiple values specified for the following single-valued field(s): tid/");
    }
}
