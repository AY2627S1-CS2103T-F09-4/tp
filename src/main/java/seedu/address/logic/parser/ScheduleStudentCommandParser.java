package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.time.DayOfWeek;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ScheduleStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.schedule.TimeSlot;

/**
 * Parses input arguments and creates a new ScheduleStudentCommand object.
 */
public class ScheduleStudentCommandParser implements Parser<ScheduleStudentCommand> {

    /**
     * Parses the given arguments in the context of ScheduleStudentCommand.
     *
     * @throws ParseException if the arguments do not conform to the expected format.
     */
    @Override
    public ScheduleStudentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DAY, PREFIX_FROM, PREFIX_TO);

        if (argMultimap.getPreamble().isEmpty()
                || argMultimap.getValue(PREFIX_DAY).isEmpty()
                || argMultimap.getValue(PREFIX_FROM).isEmpty()
                || argMultimap.getValue(PREFIX_TO).isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ScheduleStudentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DAY, PREFIX_FROM, PREFIX_TO);

        List<Index> indexes;
        try {
            indexes = ParserUtil.parseIndexes(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ScheduleStudentCommand.MESSAGE_USAGE), pe);
        }
        ParserUtil.verifyNoDuplicateIndexes(indexes);

        DayOfWeek day = ParserUtil.parseDay(argMultimap.getValue(PREFIX_DAY).get());
        TimeSlot timeSlot = ParserUtil.parseTimeSlot(
                argMultimap.getValue(PREFIX_FROM).get(), argMultimap.getValue(PREFIX_TO).get());

        return new ScheduleStudentCommand(indexes, day, timeSlot);
    }
}
