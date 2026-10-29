package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;

/**
 * Schedules one or more students, identified by displayed index, together in one session on a day and time slot.
 * Overlapping sessions are allowed.
 */
public class ScheduleStudentCommand extends Command {

    public static final String COMMAND_WORD = "schedule-student";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Schedules the students identified by the index numbers used in the displayed student list "
            + "together in one session. Give more than one index to schedule a group lesson.\n"
            + "Parameters: INDEX [MORE_INDEXES]... day/DAY from/HH-mm to/HH-mm\n"
            + "Example: " + COMMAND_WORD + " 1 2 3 day/monday from/10-00 to/12-00";

    public static final String MESSAGE_SCHEDULE_PERSON_SUCCESS = "Scheduled %1$s on %2$s from %3$s to %4$s";

    private static final Logger logger = LogsCenter.getLogger(ScheduleStudentCommand.class);

    private final List<Index> targetIndexes;
    private final DayOfWeek day;
    private final TimeSlot timeSlot;

    /**
     * Creates a ScheduleStudentCommand that schedules the students at {@code targetIndexes} in one session.
     *
     * @param targetIndexes Displayed indexes of the students; must be non-empty and contain no duplicates.
     * @param day Day of the week of the session.
     * @param timeSlot Time slot of the session.
     */
    public ScheduleStudentCommand(List<Index> targetIndexes, DayOfWeek day, TimeSlot timeSlot) {
        requireAllNonNull(targetIndexes, day, timeSlot);
        requireAllNonNull(targetIndexes);
        if (targetIndexes.isEmpty()) {
            throw new IllegalArgumentException("At least one student index is required.");
        }
        assert targetIndexes.stream().distinct().count() == targetIndexes.size()
                : "duplicate indexes should be rejected by the parser";
        this.targetIndexes = List.copyOf(targetIndexes);
        this.day = day;
        this.timeSlot = timeSlot;
    }

    /**
     * Creates a ScheduleStudentCommand for the single student at {@code targetIndex}.
     */
    public ScheduleStudentCommand(Index targetIndex, DayOfWeek day, TimeSlot timeSlot) {
        this(List.of(requireNonNull(targetIndex)), day, timeSlot);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> students = resolveStudents(model, targetIndexes);

        model.addScheduledSession(new ScheduledSession(students, day, timeSlot));
        logger.info("Scheduled a session for " + students.size() + " student(s) on " + day + " " + timeSlot);

        return new CommandResult(String.format(MESSAGE_SCHEDULE_PERSON_SUCCESS,
                formatNames(students), formatDay(day), timeSlot.getStartString(), timeSlot.getEndString()));
    }

    /**
     * Returns the students at {@code indexes} in the displayed student list, in the order given.
     *
     * @throws CommandException if any index is outside the displayed list; no student is returned in that case.
     */
    static List<Person> resolveStudents(Model model, List<Index> indexes) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();
        List<Person> students = new ArrayList<>();
        for (Index index : indexes) {
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            students.add(lastShownList.get(index.getZeroBased()));
        }
        return students;
    }

    /** Returns the names of {@code students} joined by commas, e.g. {@code "Alice, Bob"}. */
    static String formatNames(List<Person> students) {
        return students.stream()
                .map(student -> student.getName().toString())
                .collect(Collectors.joining(", "));
    }

    /** Returns {@code day} with only its first letter in upper case, e.g. {@code "Monday"}. */
    static String formatDay(DayOfWeek day) {
        return day.name().charAt(0) + day.name().substring(1).toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ScheduleStudentCommand otherCommand)) {
            return false;
        }
        return targetIndexes.equals(otherCommand.targetIndexes)
                && day == otherCommand.day
                && timeSlot.equals(otherCommand.timeSlot);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndexes", targetIndexes)
                .add("day", day)
                .add("timeSlot", timeSlot)
                .toString();
    }
}
