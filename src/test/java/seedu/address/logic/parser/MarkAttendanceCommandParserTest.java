package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.MarkAttendanceCommand;

public class MarkAttendanceCommandParserTest {
    private static final String STATUS_ERROR = "Status must be completed, absent, cancelled, or unrecorded.";
    private final MarkAttendanceCommandParser parser = new MarkAttendanceCommandParser();

    @Test
    public void parse_invalidSessionNumber_reportsSpecificReasonWithUsageAndExample() {
        assertFailure("", "A session number is required.");
        assertFailure(" date/2026-10-19 status/completed", "A session number is required.");
        for (String index : new String[] {"abc", "0", "-1", "1.5", "2147483648", "1 extra"}) {
            assertFailure(" " + index + " date/2026-10-19 status/completed",
                    "Session number must be a positive integer.");
        }
    }

    @Test
    public void parse_missingInvalidOrDuplicateFields_reportsSpecificReasonWithUsageAndExample() {
        assertFailure(" 1 status/completed", "Missing date/ argument.");
        assertFailure(" 1 date/2026-10-19", "Missing status/ argument.");
        assertFailure(" 1 date/2026-02-30 status/completed",
                "date/ must contain a valid calendar date in YYYY-MM-DD format.");
        assertFailure(" 1 date/2026-1-19 status/completed", "Use a valid date in YYYY-MM-DD format.");
        assertFailure(" 1 date/2026-10-19 status/present", STATUS_ERROR);
        assertFailure(" 1 date/2026-10-19 status/", STATUS_ERROR);
        assertFailure(" 1 unknown/value", "Unknown argument prefix: unknown/");
        for (String field : new String[] {"date", "status"}) {
            assertFailure(" 1 " + field + "/value " + field + "/value",
                    Messages.getErrorMessageForDuplicatePrefixes(new Prefix(field + "/")));
        }
    }

    private void assertFailure(String input, String reason) {
        assertParseFailure(parser, input, MarkAttendanceCommand.withUsage(reason));
    }
}
