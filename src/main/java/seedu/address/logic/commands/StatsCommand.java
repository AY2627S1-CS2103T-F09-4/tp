package seedu.address.logic.commands;

import java.time.LocalDate;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.statistics.StatisticsReport;

/** Read-only workload or student progress report with inclusive date boundaries. */
public class StatsCommand extends Command {
    private final Index studentIndex;
    private final LocalDate from;
    private final LocalDate to;

    /** Creates a report; null index selects business statistics and null dates select year-to-date. */
    public StatsCommand(Index studentIndex, LocalDate from, LocalDate to) {
        this.studentIndex = studentIndex;
        this.from = from;
        this.to = to;
    }

    /** Formats the appropriate report syntax and date-range example. */
    public static String usage(boolean studentReport) {
        String command = studentReport ? "stats-student" : "stats";
        String description = studentReport
                ? ": Shows attendance and assessment progress for a student. "
                : ": Shows workload statistics. ";
        return command + description + "Parameters: " + (studentReport ? "INDEX " : "")
                + "[from/YYYY-MM-DD to/YYYY-MM-DD]\nSupply both dates together, or omit both.\nExample: "
                + command + (studentReport ? " 1" : "") + " from/2026-01-01 to/2026-10-01";
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        LocalDate today = LocalDate.now(model.getClock());
        LocalDate start = from == null ? today.withDayOfYear(1) : from;
        LocalDate end = to == null ? today : to;
        try {
            return new CommandResult(studentIndex == null
                    ? StatisticsReport.business(model.getAddressBook(), start, end)
                    : StatisticsReport.student(model.getAddressBook(),
                            HistoryCommand.resolveStudent(model, studentIndex), start, end));
        } catch (CommandException e) {
            throw new CommandException(e.getMessage() + "\n" + usage(true), e);
        }
    }
}
