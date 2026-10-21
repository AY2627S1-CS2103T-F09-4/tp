package seedu.address.logic.parser;

import java.time.DateTimeException;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.statistics.HistoryValidation;
import seedu.address.model.statistics.LessonRecord.Outcome;

/** Parses a displayed session number, calendar date and explicit attendance outcome. */
public class MarkAttendanceCommandParser implements Parser<MarkAttendanceCommand> {
    @Override
    public MarkAttendanceCommand parse(String args) throws ParseException {
        ArgumentMultimap values = HistoryCommandParser.arguments(args, "date", "status");
        try {
            Index sessionIndex = ParserUtil.parseIndex(values.getPreamble());
            String date = values.getValue(new Prefix("date/"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing date/ argument."));
            String status = values.getValue(new Prefix("status/"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing status/ argument."));
            return new MarkAttendanceCommand(sessionIndex, HistoryValidation.date(date), Outcome.parse(status));
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new ParseException("Invalid attendance: " + e.getMessage() + "\nUsage: "
                    + MarkAttendanceCommand.MESSAGE_USAGE, e);
        }
    }
}
