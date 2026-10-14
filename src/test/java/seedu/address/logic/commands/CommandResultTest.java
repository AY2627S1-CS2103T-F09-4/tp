package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + "}";
        assertEquals(expected, commandResult.toString());
    }

    @Test
    public void constructor_withKeyword_setsFieldsCorrectly() {
        CommandResult commandResult = new CommandResult("feedback", "Alice");

        assertEquals("feedback", commandResult.getFeedbackToUser());
        assertEquals("Alice", commandResult.getKeyword());
        assertFalse(commandResult.isShowHelp());
        assertFalse(commandResult.isExit());
    }

    @Test
    public void equals_withKeyword() {
        CommandResult commandResult = new CommandResult("feedback", "Alice");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback", "Alice")));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser -> returns false
        assertFalse(commandResult.equals(new CommandResult("different", "Alice")));

        // different keyword -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", "Bob")));

        // different keyword (null) -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback")));
    }

    @Test
    public void hashcode_withKeyword() {
        CommandResult commandResult = new CommandResult("feedback", "Alice");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(),
                new CommandResult("feedback", "Alice").hashCode());

        // different feedbackToUser -> returns different hashcode
        assertNotEquals(commandResult.hashCode(),
                new CommandResult("different", "Alice").hashCode());

        // different keyword -> returns different hashcode
        assertNotEquals(commandResult.hashCode(),
                new CommandResult("feedback", "Bob").hashCode());

        // different keyword (null) -> returns different hashcode
        assertNotEquals(commandResult.hashCode(),
                new CommandResult("feedback").hashCode());
    }

    @Test
    public void constructor_withNullKeyword_setsKeywordToNull() {
        CommandResult commandResult = new CommandResult("feedback", null);

        assertEquals("feedback", commandResult.getFeedbackToUser());
        assertEquals(null, commandResult.getKeyword());
    }
}
