package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static final CommandSummary DELETE_SUMMARY =
            new CommandSummary("delete-student", "INDEX", "Deletes a student.");

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
    public void constructor_commandSummaries_cellsShowCommandCards() throws Exception {
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
    public void updateItem_emptyCell_showsNothing() throws Exception {
        CommandListPanel.CommandListViewCell cell = createCellShowing(DELETE_SUMMARY);

        FxTestSupport.runOnFxThread(() -> cell.updateItem(null, true));

        assertNull(cell.getGraphic());
        assertNull(cell.getText());
    }

    private static CommandListPanel createPanel(List<CommandSummary> commandSummaries) throws Exception {
        return FxTestSupport.runOnFxThread(() -> new CommandListPanel(commandSummaries));
    }

    /** Returns a new cell of a panel listing {@code commandSummary}, after it has been asked to show it. */
    private static CommandListPanel.CommandListViewCell createCellShowing(CommandSummary commandSummary)
            throws Exception {
        CommandListPanel panel = createPanel(List.of(commandSummary));
        return FxTestSupport.runOnFxThread(() -> {
            CommandListPanel.CommandListViewCell cell = panel.new CommandListViewCell();
            cell.updateItem(commandSummary, false);
            return cell;
        });
    }

    private static ListView<CommandSummary> listView(CommandListPanel panel)
            throws ReflectiveOperationException {
        return UiTestUtil.fieldOf(panel, "commandListView");
    }
}
