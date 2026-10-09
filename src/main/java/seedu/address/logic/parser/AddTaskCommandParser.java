package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.task.Task;

/**
 * Parses input arguments and creates a new AddTaskCommand object.
 */
public class AddTaskCommandParser implements Parser<AddTaskCommand> {

    /**
     * Parses the given arguments in the context of AddTaskCommand.
     *
     * @throws ParseException if the arguments do not conform to the expected format.
     */
    @Override
    public AddTaskCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TASK);

        if (argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_TASK).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTaskCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TASK);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTaskCommand.MESSAGE_USAGE), pe);
        }

        Task task;
        try {
            task = ParserUtil.parseTask(argMultimap.getValue(PREFIX_TASK).get());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTaskCommand.MESSAGE_USAGE), pe);
        }
        return new AddTaskCommand(index, task);
    }
}
