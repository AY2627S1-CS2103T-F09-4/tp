package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Phone;

public class DeleteParentCommandTest {
    private static final ParentContact PARENT = new ParentContact(
            new Name("Mary Tan"), new Phone("98765432"), new Email("mary@example.com"), null,
            List.of("Prefers email"));

    @Test
    public void execute_validIndex_success() throws Exception {
        ModelManager model = new ModelManager();
        model.addParentContact(PARENT);

        CommandResult result = new DeleteParentCommand(Index.fromOneBased(1)).execute(model);

        assertEquals(String.format(DeleteParentCommand.MESSAGE_DELETE_PARENT_SUCCESS, Messages.format(PARENT)),
                result.getFeedbackToUser());
        assertTrue(model.getParentContactList().isEmpty());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PARENT_DISPLAYED_INDEX, () ->
                new DeleteParentCommand(Index.fromOneBased(1)).execute(new ModelManager()));
    }
}
