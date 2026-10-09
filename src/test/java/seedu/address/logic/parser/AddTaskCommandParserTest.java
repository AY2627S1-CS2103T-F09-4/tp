package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddTaskCommand;
import seedu.address.model.task.Task;

public class AddTaskCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTaskCommand.MESSAGE_USAGE);

    private final AddTaskCommandParser parser = new AddTaskCommandParser();

    @Test
    public void parse_validMultiwordDescription_returnsAddTaskCommand() {
        assertParseSuccess(parser, "1 " + PREFIX_TASK + "do homework tonight",
                new AddTaskCommand(INDEX_FIRST_PERSON, new Task("do homework tonight")));
    }

    @Test
    public void parse_trimsDescriptionWhitespace() {
        assertParseSuccess(parser, "1 " + PREFIX_TASK + "  do homework  ",
                new AddTaskCommand(INDEX_FIRST_PERSON, new Task("do homework")));
    }

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, PREFIX_TASK + "do homework", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 " + PREFIX_TASK + "   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "0 " + PREFIX_TASK + "do homework", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_duplicateTaskPrefix_failure() {
        assertParseFailure(parser, "1 " + PREFIX_TASK + "do homework " + PREFIX_TASK + "revise notes",
                "Multiple values specified for the following single-valued field(s): task/");
    }
}