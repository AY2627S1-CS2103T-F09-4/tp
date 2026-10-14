package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_ID;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.MarkTaskCommand;
import seedu.address.model.task.TaskStatus;

public class MarkTaskCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkTaskCommand.MESSAGE_USAGE);

    private final MarkTaskCommandParser parser = new MarkTaskCommandParser();

    @Test
    public void parse_done_returnsMarkTaskCommand() {
        assertParseSuccess(parser, "1 " + PREFIX_TASK_ID + "1 " + PREFIX_STATUS + "done",
                new MarkTaskCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON, TaskStatus.DONE));
    }

    @Test
    public void parse_notDoneWithSpace_returnsMarkTaskCommand() {
        assertParseSuccess(parser, "2 " + PREFIX_TASK_ID + "1 " + PREFIX_STATUS + "not done",
                new MarkTaskCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON, TaskStatus.NOT_DONE));
    }

    @Test
    public void parse_ignoresCaseAndWhitespace() {
        assertParseSuccess(parser, "1 " + PREFIX_TASK_ID + " 2 " + PREFIX_STATUS + "  NOT DONE  ",
                new MarkTaskCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, TaskStatus.NOT_DONE));
    }

    @Test
    public void parse_prefixOrderDoesNotMatter() {
        assertParseSuccess(parser, "1 " + PREFIX_STATUS + "done " + PREFIX_TASK_ID + "2",
                new MarkTaskCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, TaskStatus.DONE));
    }

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_STATUS + "done", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, PREFIX_TASK_ID + "1 " + PREFIX_STATUS + "done", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + " " + PREFIX_STATUS + "done", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndexes_failure() {
        assertParseFailure(parser, "0 " + PREFIX_TASK_ID + "1 " + PREFIX_STATUS + "done", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 2 " + PREFIX_TASK_ID + "1 " + PREFIX_STATUS + "done", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "0 " + PREFIX_STATUS + "done", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "-1 " + PREFIX_STATUS + "done", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "a " + PREFIX_STATUS + "done", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidStatus_failure() {
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "1 " + PREFIX_STATUS + "finished",
                TaskStatus.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicatePrefixes_failure() {
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "1 " + PREFIX_TASK_ID + "2 " + PREFIX_STATUS + "done",
                "Multiple values specified for the following single-valued field(s): tid/");
        assertParseFailure(parser, "1 " + PREFIX_TASK_ID + "1 " + PREFIX_STATUS + "done " + PREFIX_STATUS
                + "not done", "Multiple values specified for the following single-valued field(s): status/");
    }
}
