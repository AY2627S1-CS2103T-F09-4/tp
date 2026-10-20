package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.storage.SnapshotManager;

public class RedoCommandTest {

    private Model model;
    private RedoCommand redoCommand;

    @BeforeEach
    public void setUp() {
        SnapshotManager.reset();
        model = new ModelManager();
        redoCommand = new RedoCommand();
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(
                NullPointerException.class, () -> redoCommand.execute(null));
    }

    @Test
    public void execute_noRedoAvailable_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> redoCommand.execute(model));

        assertEquals(
                Messages.MESSAGE_UNABLE_TO_REDO,
                exception.getMessage());
    }

    @Test
    public void execute_redoAvailable_returnsSuccess() throws Exception {
        // Create a second snapshot.
        model.save();

        // Undo to make redo available.
        model.undo();

        CommandResult result = redoCommand.execute(model);

        assertEquals(
                RedoCommand.MESSAGE_SUCCESS,
                result.getFeedbackToUser());
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());
    }
}
