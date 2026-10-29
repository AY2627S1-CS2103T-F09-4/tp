package seedu.address.logic.commands;

import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class CommandSummaryTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandSummary(null, "", "Lists all students."));
        assertThrows(NullPointerException.class, () ->
                new CommandSummary("list", null, "Lists all students."));
        assertThrows(NullPointerException.class, () -> new CommandSummary("list", "", null));
    }

    @Test
    public void constructor_invalidCommandWord_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CommandSummary("", "", "Lists all students."));
        assertThrows(IllegalArgumentException.class, () ->
                new CommandSummary("  ", "", "Lists all students."));
        assertThrows(IllegalArgumentException.class, () ->
                new CommandSummary("list all", "", "Lists all students."));
    }

    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CommandSummary("list", "", ""));
        assertThrows(IllegalArgumentException.class, () -> new CommandSummary("list", "", "   "));
    }
}
