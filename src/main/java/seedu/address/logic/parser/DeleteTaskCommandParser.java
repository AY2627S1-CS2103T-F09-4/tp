package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_ID;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteTaskCommand object.
 */
public class DeleteTaskCommandParser implements Parser<DeleteTaskCommand> {

    /**
     * Parses the given arguments in the context of DeleteTaskCommand.
     *
     * @throws ParseException if the arguments do not conform to the expected format.
     */
    @Override
    public DeleteTaskCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TASK_ID);

        if (argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_TASK_ID).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTaskCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TASK_ID);

        Index index;
        Index taskIndex;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
            taskIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_TASK_ID).get());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteTaskCommand.MESSAGE_USAGE),
                    pe);
        }

        return new DeleteTaskCommand(index, taskIndex);
    }
}
