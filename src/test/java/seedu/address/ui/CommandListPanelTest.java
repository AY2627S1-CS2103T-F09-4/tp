package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import seedu.address.logic.commands.CommandSummary;
import seedu.address.logic.commands.HelpCommand;

public class CommandListPanelTest {

    private static final CommandSummary DELETE_SUMMARY = new CommandSummary("delete", "INDEX", "Deletes a student.");

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
    }

    @Test
    public void constructor_commandSummaries_showsEveryCommandInOrder() throws Exception {
        CommandListPanel panel = createPanel(HelpCommand.COMMAND_SUMMARIES);

        assertEquals(HelpCommand.COMMAND_SUMMARIES, listView(panel).getItems());
    }

    @Test
    public void constructor_commandSummaries_listCannotBeModified() throws Exception {
        CommandListPanel panel = createPanel(List.of(DELETE_SUMMARY));

        assertThrows(UnsupportedOperationException.class, () -> listView(panel).getItems().clear());
    }

    @Test
    public void constructor_nullCommandSummaries_throwsNullPointerException() {
        ExecutionException thrown = assertThrows(ExecutionException.class, () -> createPanel(null));
        assertEquals(NullPointerException.class, thrown.getCause().getClass());
    }

    @Test
    public void shownInScene_showsCommandCardsInCells() throws Exception {
        CommandListPanel panel = createPanel(List.of(DELETE_SUMMARY));

        boolean hasCommandCard = FxTestSupport.runOnFxThread(() -> {
            new Scene(panel.getRoot(), 600, 400);
            panel.getRoot().applyCss();
            panel.getRoot().layout();
            return panel.getRoot().lookupAll(".list-cell").stream()
                    .anyMatch(node -> ((ListCell<?>) node).getGraphic() != null);
        });

        assertTrue(hasCommandCard);
    }

    @Test
    public void updateItem_commandSummary_showsCommandCard() throws Exception {
        CommandListPanel panel = createPanel(List.of(DELETE_SUMMARY));

        CommandListPanel.CommandListViewCell cell = FxTestSupport.runOnFxThread(() -> {
            CommandListPanel.CommandListViewCell newCell = panel.new CommandListViewCell();
            newCell.updateItem(DELETE_SUMMARY, false);
            return newCell;
        });

        assertNotNull(cell.getGraphic());
    }

    @Test
    public void updateItem_emptyCell_showsNothing() throws Exception {
        CommandListPanel panel = createPanel(List.of(DELETE_SUMMARY));

        CommandListPanel.CommandListViewCell cell = FxTestSupport.runOnFxThread(() -> {
            CommandListPanel.CommandListViewCell newCell = panel.new CommandListViewCell();
            newCell.updateItem(DELETE_SUMMARY, false);
            newCell.updateItem(null, true);
            return newCell;
        });

        assertNull(cell.getGraphic());
        assertNull(cell.getText());
    }

    private static CommandListPanel createPanel(List<CommandSummary> commandSummaries) throws Exception {
        return FxTestSupport.runOnFxThread(() -> new CommandListPanel(commandSummaries));
    }

    @SuppressWarnings("unchecked")
    private static ListView<CommandSummary> listView(CommandListPanel panel) throws ReflectiveOperationException {
        Field field = CommandListPanel.class.getDeclaredField("commandListView");
        field.setAccessible(true);
        return (ListView<CommandSummary>) field.get(panel);
    }
}
