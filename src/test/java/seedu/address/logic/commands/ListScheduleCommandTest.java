package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.ModelManager;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.statistics.LessonRecord.Outcome;
import seedu.address.testutil.ScheduledSessionBuilder;

public class ListScheduleCommandTest {
    @Test
    public void execute_emptySchedule_readableEmptyOutput() {
        assertEquals("Weekly schedule: none",
                new ListScheduleCommand().execute(new ModelManager()).getFeedbackToUser());
    }

    @Test
    public void execute_unsortedSchedule_sortsRowsButPreservesNumbersAndAttendanceTargets() throws Exception {
        ModelManager model = new ModelManager();
        List<ScheduledSession> original = new ArrayList<>();
        for (DayOfWeek day : List.of(DayOfWeek.SUNDAY, DayOfWeek.FRIDAY, DayOfWeek.TUESDAY,
                DayOfWeek.SATURDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.MONDAY)) {
            original.add(new ScheduledSessionBuilder().withDay(day).withTimeSlot("16-00", "17-00").build());
        }
        original.add(new ScheduledSessionBuilder().withDay(DayOfWeek.MONDAY)
                .withTimeSlot("09-00", "10-00").build());
        original.add(new ScheduledSessionBuilder().withDay(DayOfWeek.MONDAY)
                .withTimeSlot("16-00", "18-00").build());
        original.forEach(model::addScheduledSession);
        model.updateFilteredPersonList(student -> false);

        String output = new ListScheduleCommand().execute(model).getFeedbackToUser();
        List<Integer> numbers = output.lines().skip(1)
                .map(line -> Integer.parseInt(line.substring(0, line.indexOf('.')))).toList();
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), numbers);
        List<Integer> sessionNumbers = output.lines().skip(1)
                .map(line -> Integer.parseInt(line.substring(line.lastIndexOf('#') + 1))).toList();
        assertEquals(List.of(8, 7, 9, 3, 5, 6, 2, 4, 1), sessionNumbers);
        assertEquals(original, model.getScheduleList());

        new MarkAttendanceCommand(Index.fromOneBased(sessionNumbers.getFirst()),
                LocalDate.of(2026, 10, 19), Outcome.COMPLETED).execute(model);
        assertEquals(original.get(7).getSessionId(),
                model.getAddressBook().getLessonRecords().getFirst().sourceSessionId());
    }
}
