package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.UnscheduleStudentCommand;
import seedu.address.model.schedule.TimeSlot;

public class UnscheduleStudentCommandParserTest {

    private static final String INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnscheduleStudentCommand.MESSAGE_USAGE);

    private final UnscheduleStudentCommandParser parser = new UnscheduleStudentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccess(parser, " 1 day/monday from/10-00 to/12-00",
                new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, TimeSlot.of("10-00", "12-00")));
    }

    @Test
    public void parse_prefixesInAnyOrder_success() {
        assertParseSuccess(parser, " 2 to/12-00 from/10-00 day/friday",
                new UnscheduleStudentCommand(INDEX_SECOND_PERSON, DayOfWeek.FRIDAY, TimeSlot.of("10-00", "12-00")));
    }

    @Test
    public void parse_dayVariants_success() {
        TimeSlot slot = TimeSlot.of("10-00", "12-00");
        assertParseSuccess(parser, " 1 day/MONDAY from/10-00 to/12-00",
                new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.MONDAY, slot));
        assertParseSuccess(parser, " 1 day/Tue from/10-00 to/12-00",
                new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.TUESDAY, slot));
        assertParseSuccess(parser, " 1 day/  sun  from/10-00 to/12-00",
                new UnscheduleStudentCommand(INDEX_FIRST_PERSON, DayOfWeek.SUNDAY, slot));
    }

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "", INVALID_FORMAT);
        assertParseFailure(parser, " day/monday from/10-00 to/12-00", INVALID_FORMAT);
        assertParseFailure(parser, " 1 from/10-00 to/12-00", INVALID_FORMAT);
        assertParseFailure(parser, " 1 day/monday to/12-00", INVALID_FORMAT);
        assertParseFailure(parser, " 1 day/monday from/10-00", INVALID_FORMAT);
        assertParseFailure(parser, " 1", INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, " a day/monday from/10-00 to/12-00", INVALID_FORMAT);
        assertParseFailure(parser, " 0 day/monday from/10-00 to/12-00", INVALID_FORMAT);
        assertParseFailure(parser, " -1 day/monday from/10-00 to/12-00", INVALID_FORMAT);
        assertParseFailure(parser, " 1 a day/monday from/10-00 to/12-00", INVALID_FORMAT);
    }

    @Test
    public void parse_multipleIndexes_success() {
        assertParseSuccess(parser, " 2 1 day/monday from/10-00 to/12-00",
                new UnscheduleStudentCommand(List.of(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON), DayOfWeek.MONDAY,
                        TimeSlot.of("10-00", "12-00")));
    }

    @Test
    public void parse_duplicateIndexes_failure() {
        assertParseFailure(parser, " 2 2 day/monday from/10-00 to/12-00", ParserUtil.MESSAGE_DUPLICATE_INDEX);
    }

    @Test
    public void parse_duplicatePrefixes_failure() {
        assertParseFailure(parser, " 1 day/monday day/tuesday from/10-00 to/12-00", duplicateMessage("day/"));
        assertParseFailure(parser, " 1 day/monday from/10-00 from/11-00 to/12-00", duplicateMessage("from/"));
        assertParseFailure(parser, " 1 day/monday from/10-00 to/12-00 to/13-00", duplicateMessage("to/"));
    }

    @Test
    public void parse_invalidDay_failure() {
        assertParseFailure(parser, " 1 day/someday from/10-00 to/12-00", ParserUtil.MESSAGE_INVALID_DAY);
        assertParseFailure(parser, " 1 day/mo from/10-00 to/12-00", ParserUtil.MESSAGE_INVALID_DAY);
        assertParseFailure(parser, " 1 day/ from/10-00 to/12-00", ParserUtil.MESSAGE_INVALID_DAY);
    }

    @Test
    public void parse_invalidTimes_failure() {
        assertParseFailure(parser, " 1 day/monday from/10:00 to/12:00", TimeSlot.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 day/monday from/1000 to/1200", TimeSlot.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 day/monday from/25-00 to/26-00", TimeSlot.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 day/monday from/12-00 to/10-00", TimeSlot.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 day/monday from/10-00 to/10-00", TimeSlot.MESSAGE_CONSTRAINTS);
    }

    private static String duplicateMessage(String prefix) {
        return Messages.getErrorMessageForDuplicatePrefixes(new Prefix(prefix));
    }
}
