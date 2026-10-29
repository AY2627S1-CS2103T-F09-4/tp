package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class HistoryCommandTest {

    @Test
    public void usage_editAssessment_showsDescriptionAndExample() {
        String usage = HistoryCommand.usage("edit-assessment");
        assertTrue(usage.startsWith("edit-assessment: Replaces an assessment record."));
        assertTrue(usage.contains("Example: edit-assessment 1 "));
    }

    @Test
    public void usage_unknownOperation_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> HistoryCommand.usage("unknown"));
    }
}
