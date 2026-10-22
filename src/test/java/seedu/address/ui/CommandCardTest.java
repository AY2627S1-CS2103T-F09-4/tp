package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import seedu.address.logic.commands.CommandSummary;

public class CommandCardTest {

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
    }

    @Test
    public void constructor_commandWithFormat_showsWordFormatAndDescription() throws Exception {
        CommandCard card = createCard(new CommandSummary("delete", "INDEX", "Deletes a student."));

        assertEquals("delete", textOf(card, "commandWord"));
        assertEquals("Format: delete INDEX", textOf(card, "format"));
        assertEquals("Deletes a student.", textOf(card, "description"));
    }

    @Test
    public void constructor_commandWithoutFormat_showsFormatWithoutTrailingSpace() throws Exception {
        CommandCard card = createCard(new CommandSummary("list", "", "Lists all students."));

        assertEquals("Format: list", textOf(card, "format"));
    }

    @Test
    public void constructor_nullCommandSummary_throwsNullPointerException() {
        ExecutionException thrown = assertThrows(ExecutionException.class, () -> createCard(null));
        assertEquals(NullPointerException.class, thrown.getCause().getClass());
    }

    private static CommandCard createCard(CommandSummary commandSummary) throws Exception {
        return FxTestSupport.runOnFxThread(() -> new CommandCard(commandSummary));
    }

    private static String textOf(CommandCard card, String fieldName) throws ReflectiveOperationException {
        Field field = CommandCard.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return ((Label) field.get(card)).getText();
    }
}
