package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_ID;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.task.TaskStatus;

/**
 * Parses input arguments and creates a new MarkTaskCommand object.
 */
public class MarkTaskCommandParser implements Parser<MarkTaskCommand> {

    /**
     * Parses the given arguments in the context of MarkTaskCommand.
     *
     * @throws ParseException if the arguments do not conform to the expected format.
     */
    @Override
    public MarkTaskCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TASK_ID, PREFIX_STATUS);

        if (argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_TASK_ID).isEmpty()
                || argMultimap.getValue(PREFIX_STATUS).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkTaskCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TASK_ID, PREFIX_STATUS);

        Index index;
        Index taskIndex;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
            taskIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_TASK_ID).get());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkTaskCommand.MESSAGE_USAGE),
                    pe);
        }

        TaskStatus status = ParserUtil.parseTaskStatus(argMultimap.getValue(PREFIX_STATUS).get());
        return new MarkTaskCommand(index, taskIndex, status);
    }
}
