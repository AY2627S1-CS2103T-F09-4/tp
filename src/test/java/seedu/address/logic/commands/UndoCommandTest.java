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

public class UndoCommandTest {

    private Model model;
    private UndoCommand undoCommand;

    @BeforeEach
    public void setUp() {
        SnapshotManager.reset();
        model = new ModelManager();
        undoCommand = new UndoCommand();
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> undoCommand.execute(null));
    }

    @Test
    public void execute_noUndoAvailable_throwsCommandException() {
        CommandException exception = assertThrows(
                CommandException.class, () -> undoCommand.execute(model));

        assertEquals(Messages.MESSAGE_UNABLE_TO_UNDO, exception.getMessage());
    }

    @Test
    public void execute_undoAvailable_returnsSuccess() throws Exception {
        // Save a second snapshot so there is a previous state to undo.
        model.save();

        CommandResult result = undoCommand.execute(model);

        assertEquals(UndoCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());
    }
}
