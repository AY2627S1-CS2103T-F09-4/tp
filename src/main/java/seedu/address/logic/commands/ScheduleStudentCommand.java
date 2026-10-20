package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;

/**
 * Schedules a person, identified by displayed index, on a day and time slot.
 * Overlapping sessions are allowed.
 */
public class ScheduleStudentCommand extends Command {

    public static final String COMMAND_WORD = "schedule-student";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Schedules the student identified by the index number used in the displayed student list.\n"
            + "Parameters: INDEX day/DAY from/HH-mm to/HH-mm\n"
            + "Example: " + COMMAND_WORD + " 1 day/monday from/10-00 to/12-00";

    public static final String MESSAGE_SCHEDULE_PERSON_SUCCESS = "Scheduled %1$s on %2$s from %3$s to %4$s";

    private final Index targetIndex;
    private final DayOfWeek day;
    private final TimeSlot timeSlot;

    /**
     * Creates a ScheduleStudentCommand for the person at {@code targetIndex}.
     */
    public ScheduleStudentCommand(Index targetIndex, DayOfWeek day, TimeSlot timeSlot) {
        requireAllNonNull(targetIndex, day, timeSlot);
        this.targetIndex = targetIndex;
        this.day = day;
        this.timeSlot = timeSlot;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person person = lastShownList.get(targetIndex.getZeroBased());
        model.addScheduledSession(new ScheduledSession(person, day, timeSlot));

        String dayName = day.name().charAt(0) + day.name().substring(1).toLowerCase(Locale.ROOT);
        return new CommandResult(String.format(MESSAGE_SCHEDULE_PERSON_SUCCESS,
                person.getName(), dayName, timeSlot.getStartString(), timeSlot.getEndString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ScheduleStudentCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex)
                && day == otherCommand.day
                && timeSlot.equals(otherCommand.timeSlot);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("day", day)
                .add("timeSlot", timeSlot)
                .toString();
    }
}
