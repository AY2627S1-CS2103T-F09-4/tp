package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.HelpCommand.COMMAND_SUMMARIES;
import static seedu.address.logic.commands.HelpCommand.SHOWING_HELP_MESSAGE;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_help_success() {
        CommandResult expectedCommandResult = new CommandResult(SHOWING_HELP_MESSAGE, true, false, false, false);
        assertCommandSuccess(new HelpCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new HelpCommand().execute(null));
    }

    @Test
    public void commandSummaries_noDuplicateCommandWords() {
        long uniqueCommandWords = COMMAND_SUMMARIES.stream().map(CommandSummary::commandWord)
                .distinct().count();
        assertEquals(COMMAND_SUMMARIES.size(), uniqueCommandWords);
    }

    @Test
    public void commandSummaries_everyCommandWordIsKnown() {
        AddressBookParser parser = new AddressBookParser();
        for (CommandSummary summary : COMMAND_SUMMARIES) {
            try {
                parser.parseCommand(summary.commandWord());
            } catch (ParseException e) {
                // Commands with required parameters fail to parse, but must not be unknown
                assertNotEquals(MESSAGE_UNKNOWN_COMMAND, e.getMessage(), summary.commandWord());
            }
        }
    }
}
