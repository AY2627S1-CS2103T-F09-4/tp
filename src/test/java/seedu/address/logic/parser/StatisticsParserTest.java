package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.StatsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class StatisticsParserTest {
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void stats_requiresBothValidOrderedDatesAndDisplayedIndex() throws Exception {
        assertTrue(parser.parseCommand("stats") instanceof StatsCommand);
        assertTrue(parser.parseCommand("stats-student 1 from/2024-02-29 to/2024-02-29").isReadOnly());
        String[] invalid = {"stats from/2026-01-01", "stats to/2026-02-01", "stats from/ to/2026-01-01",
            "stats from/2026-02-30 to/2026-03-01", "stats from/2026-02-01 to/2026-01-01", "stats 1",
            "stats from/2026-1-01 to/2026-02-01", "stats from/2026-01-01 from/2026-01-02 to/2026-01-03",
            "stats-student", "stats-student 0", "stats-student -1", "stats-student 2147483648",
            "stats student/1", "stats-student 1 to/2026-01-01"};
        for (String input : invalid) {
            assertThrows(ParseException.class, () -> parser.parseCommand(input), input);
        }
    }

    @Test
    public void lesson_rejectsInvalidDateTimeStatusAndPrefixes() throws Exception {
        String valid = "record-lesson 1 date/2026-10-01 from/10:00 to/11:00 status/completed";
        parser.parseCommand(valid);
        String[] invalid = {valid.replace("2026-10-01", "2026-02-29"), valid.replace("10:00", "24:00"),
            valid.replace("10:00", "10-00"), valid.replace("10:00", "11:00"),
            valid.replace("11:00", "09:00"), valid.replace("completed", "present"),
            valid.replace("date/2026-10-01 ", ""), valid + " status/absent", valid + " typo/value",
            valid + " subject/Math", valid.replace("lesson 1", "lesson 0")};
        for (String input : invalid) {
            assertThrows(ParseException.class, () -> parser.parseCommand(input), input);
        }
    }

    @Test
    public void assessment_rejectsInvalidMarksAndMissingFields() throws Exception {
        String valid = "record-assessment 1 date/2026-10-01 subject/Math category/Quiz title/First test score/4 max/5";
        parser.parseCommand(valid);
        String[] invalid = {valid.replace("score/4", "score/-1"), valid.replace("score/4", "score/6"),
            valid.replace("max/5", "max/0"), valid.replace("max/5", "max/-5"),
            valid.replace("score/4", "score/NaN"), valid.replace("score/4", "score/Infinity"),
            valid.replace("title/First test", "title/"), valid.replace("category/Quiz", ""),
            valid + " max/10", valid + " unknown/a"};
        for (String input : invalid) {
            assertThrows(ParseException.class, () -> parser.parseCommand(input), input);
        }
    }

    @Test
    public void correctionDeletionAndLists_useRecordNumbersAndExactArguments() throws Exception {
        String id = "1";
        parser.parseCommand("edit-lesson " + id
                + " date/2026-10-01 from/10:00 to/11:00 status/absent subject/Math");
        parser.parseCommand("edit-assessment " + id
                + " date/2026-10-01 subject/Math category/Quiz title/Test score/0 max/10");
        parser.parseCommand("delete-lesson " + id);
        parser.parseCommand("delete-assessment " + id);
        for (String input : new String[]{"list-lessons", "list-assessments"}) {
            Command command = parser.parseCommand(input);
            assertTrue(command.isReadOnly());
            assertThrows(ParseException.class, () -> parser.parseCommand(input + " 1"));
        }
        assertThrows(ParseException.class, () ->
                parser.parseCommand("delete-lesson 00000000-0000-0000-0000-000000000001"));
        assertThrows(ParseException.class, () -> parser.parseCommand("delete-assessment " + id + " extra"));
    }

    @Test
    public void numberedCommands_rejectNonpositiveNonnumericOverflowAndUuidTargets() {
        String[] suffixes = {"delete-lesson %s", "delete-assessment %s",
            "edit-lesson %s date/2026-10-01 from/10:00 to/11:00 status/absent subject/Math",
            "edit-assessment %s date/2026-10-01 subject/Math category/Quiz title/Test score/0 max/10",
            "mark-attendance %s date/2026-10-19 status/completed"};
        for (String suffix : suffixes) {
            for (String invalid : new String[]{"", "0", "-1", "abc", "1.5", "2147483648",
                "00000000-0000-0000-0000-000000000001"}) {
                String input = String.format(suffix, invalid);
                assertThrows(ParseException.class, () -> parser.parseCommand(input), input);
            }
        }
    }
}
