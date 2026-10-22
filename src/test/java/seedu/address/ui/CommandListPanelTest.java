package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import seedu.address.logic.commands.CommandSummary;
import seedu.address.logic.commands.HelpCommand;

public class CommandListPanelTest {

    private static final CommandSummary DELETE_SUMMARY = new CommandSummary("delete", "INDEX", "Deletes a student.");

    private final List<URI> openedPages = new ArrayList<>();
    private final List<String> copiedTexts = new ArrayList<>();

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

    @Test
    public void constructor_nullBrowser_throwsNullPointerException() {
        ExecutionException thrown = assertThrows(ExecutionException.class, () -> FxTestSupport.runOnFxThread(() ->
                new CommandListPanel(List.of(DELETE_SUMMARY), null, copiedTexts::add, Runnable::run)));
        assertEquals(NullPointerException.class, thrown.getCause().getClass());
    }

    @Test
    public void openUserGuide_browserAvailable_opensUserGuide() throws Exception {
        CommandListPanel panel = createPanelWithBrowser(Optional.of(openedPages::add));

        FxTestSupport.runOnFxThread(panel::openUserGuide);

        assertEquals(List.of(URI.create(HelpWindow.USERGUIDE_URL)), openedPages);
        assertEquals(List.of(), copiedTexts);
        assertNotEquals(CommandListPanel.LINK_COPIED_TEXT, userGuideLink(panel).getText());
    }

    @Test
    public void openUserGuide_noBrowser_copiesUserGuideUrl() throws Exception {
        CommandListPanel panel = createPanelWithBrowser(Optional.empty());

        FxTestSupport.runOnFxThread(panel::openUserGuide);

        assertEquals(List.of(HelpWindow.USERGUIDE_URL), copiedTexts);
        assertEquals(CommandListPanel.LINK_COPIED_TEXT, userGuideLink(panel).getText());
    }

    @Test
    public void openUserGuide_browserFails_copiesUserGuideUrl() throws Exception {
        CommandListPanel panel = createPanelWithBrowser(Optional.of(uri -> {
            throw new IOException("No browser");
        }));

        FxTestSupport.runOnFxThread(panel::openUserGuide);
        // Waits for the copy that the failed browser queued on the JavaFX Application Thread
        FxTestSupport.runOnFxThread(() -> { });

        assertEquals(List.of(HelpWindow.USERGUIDE_URL), copiedTexts);
        assertEquals(CommandListPanel.LINK_COPIED_TEXT, userGuideLink(panel).getText());
    }

    private static CommandListPanel createPanel(List<CommandSummary> commandSummaries) throws Exception {
        return FxTestSupport.runOnFxThread(() -> new CommandListPanel(commandSummaries));
    }

    /**
     * Creates a panel that opens pages with {@code browser} on the calling thread, and records copied text.
     */
    private CommandListPanel createPanelWithBrowser(Optional<CommandListPanel.Browser> browser) throws Exception {
        return FxTestSupport.runOnFxThread(() ->
                new CommandListPanel(List.of(DELETE_SUMMARY), browser, copiedTexts::add, Runnable::run));
    }

    private static Hyperlink userGuideLink(CommandListPanel panel) throws ReflectiveOperationException {
        Field field = CommandListPanel.class.getDeclaredField("userGuideLink");
        field.setAccessible(true);
        return (Hyperlink) field.get(panel);
    }

    @SuppressWarnings("unchecked")
    private static ListView<CommandSummary> listView(CommandListPanel panel) throws ReflectiveOperationException {
        Field field = CommandListPanel.class.getDeclaredField("commandListView");
        field.setAccessible(true);
        return (ListView<CommandSummary>) field.get(panel);
    }
}
