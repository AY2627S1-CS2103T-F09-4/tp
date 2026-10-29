package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ViewChildCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewChildCommand object.
 */
public class ViewChildCommandParser implements Parser<ViewChildCommand> {

    /**
     * Parses the given arguments in the context of ViewChildCommand.
     *
     * @throws ParseException if the arguments are not a single positive parent index.
     */
    @Override
    public ViewChildCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            return new ViewChildCommand(ParserUtil.parseIndex(args));
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewChildCommand.MESSAGE_USAGE), pe);
        }
    }
}
