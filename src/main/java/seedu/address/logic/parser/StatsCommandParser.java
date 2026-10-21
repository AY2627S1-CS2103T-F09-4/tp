package seedu.address.logic.parser;

import java.time.DateTimeException;
import java.time.LocalDate;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.StatsCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.statistics.HistoryValidation;

/** Parses stats and stats-student, requiring either both date boundaries or neither. */
public class StatsCommandParser implements Parser<StatsCommand> {
    private final boolean studentReport;

    public StatsCommandParser(boolean studentReport) {
        this.studentReport = studentReport;
    }

    @Override
    public StatsCommand parse(String args) throws ParseException {
        ArgumentMultimap values = HistoryCommandParser.arguments(args, "from", "to");
        Index student = studentReport ? ParserUtil.parseIndex(values.getPreamble()) : null;
        boolean hasFrom = values.getValue(new Prefix("from/")).isPresent();
        boolean hasTo = values.getValue(new Prefix("to/")).isPresent();
        if ((!studentReport && !values.getPreamble().isEmpty()) || hasFrom != hasTo) {
            throw new ParseException("Usage: stats [from/YYYY-MM-DD to/YYYY-MM-DD] or "
                    + "stats-student INDEX [from/YYYY-MM-DD to/YYYY-MM-DD]. Supply both dates together.");
        }
        try {
            LocalDate from = hasFrom ? HistoryValidation.date(values.getValue(new Prefix("from/")).orElseThrow())
                    : null;
            LocalDate to = hasTo ? HistoryValidation.date(values.getValue(new Prefix("to/")).orElseThrow()) : null;
            if (from != null && from.isAfter(to)) {
                throw new IllegalArgumentException("from must be on or before to.");
            }
            return new StatsCommand(student, from, to);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new ParseException("Invalid statistics date range: " + e.getMessage(), e);
        }
    }
}
