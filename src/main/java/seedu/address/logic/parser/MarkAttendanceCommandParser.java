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
        try {
            ArgumentMultimap values = HistoryCommandParser.arguments(args, "date", "status");
            if (values.getPreamble().isEmpty()) {
                throw new ParseException("A session number is required.");
            }
            Index sessionIndex;
            try {
                sessionIndex = ParserUtil.parseIndex(values.getPreamble());
            } catch (ParseException e) {
                throw new ParseException("Session number must be a positive integer.", e);
            }
            String date = values.getValue(new Prefix("date/"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing date/ argument."));
            String status = values.getValue(new Prefix("status/"))
                    .orElseThrow(() -> new IllegalArgumentException("Missing status/ argument."));
            return new MarkAttendanceCommand(sessionIndex, HistoryValidation.date(date), Outcome.parse(status));
        } catch (DateTimeException e) {
            throw new ParseException(MarkAttendanceCommand.withUsage(
                    "date/ must contain a valid calendar date in YYYY-MM-DD format."), e);
        } catch (ParseException | IllegalArgumentException e) {
            throw new ParseException(MarkAttendanceCommand.withUsage(e.getMessage()), e);
        }
    }
}
