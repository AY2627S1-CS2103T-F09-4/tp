package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainWindowShortcutTest {
    @TempDir
    public Path folder;

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
        FxTestSupport.runOnFxThread(() -> javafx.application.Platform.setImplicitExit(false));
    }

    @Test
    public void escapeThenEnter_inCommandBox_closesWindow() throws Exception {
        checkShownWindow(stage -> {
            Node commandBox = stage.getScene().lookup("#commandTextField");
            pressKey(commandBox, KeyCode.ESCAPE);
            assertTrue(fileMenuOf(stage).isShowing());
            assertTrue(stage.isShowing());

            pressKey(commandBox, KeyCode.ENTER);
            assertFalse(stage.isShowing());
        });
    }

    @Test
    public void escape_inResultDisplay_opensFileMenu() throws Exception {
        checkShownWindow(stage -> {
            pressKey(stage.getScene().lookup("#resultDisplay"), KeyCode.ESCAPE);
            assertTrue(fileMenuOf(stage).isShowing());
        });
    }

    @Test
    public void escapeTwice_closesFileMenuWithoutClosingWindow() throws Exception {
        checkShownWindow(stage -> {
            Node commandBox = stage.getScene().lookup("#commandTextField");
            pressKey(commandBox, KeyCode.ESCAPE);
            pressKey(commandBox, KeyCode.ESCAPE);
            assertFalse(fileMenuOf(stage).isShowing());
            assertTrue(stage.isShowing());
        });
    }

    @Test
    public void escape_whileMenuBarIsActive_leavesMenuWithoutClosingWindow() throws Exception {
        checkShownWindow(stage -> {
            Node commandBox = stage.getScene().lookup("#commandTextField");
            pressKey(commandBox, KeyCode.F10);
            pressKey(commandBox, KeyCode.ESCAPE);
            assertFalse(fileMenuOf(stage).isShowing());
            assertTrue(stage.isShowing());
        });
    }

    private static Menu fileMenuOf(Stage stage) {
        return ((MenuBar) stage.getScene().lookup("#menuBar")).getMenus().get(0);
    }

    /**
     * Shows a main window with the command box focused, runs {@code check} on its stage, then closes it.
     */
    private void checkShownWindow(Consumer<Stage> check) throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(folder.resolve("book.json")),
                    new JsonUserPrefsStorage(folder.resolve("prefs.json")));
            Stage stage = new Stage();
            new MainWindow(stage, new LogicManager(new ModelManager(), storage), folder.resolve("book.json"))
                    .fillInnerParts();
            stage.show();
            stage.getScene().lookup("#commandTextField").requestFocus();
            try {
                check.accept(stage);
            } finally {
                stage.close();
            }
        });
    }

    /**
     * Presses and releases {@code code} on {@code target}. Keys go to the main window even while a menu is open,
     * as real key presses do, and the window passes them on to the open menu.
     */
    private static void pressKey(Node target, KeyCode code) {
        Event.fireEvent(target, new KeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED, "", code,
                false, false, false, false));
        Event.fireEvent(target, new KeyEvent(KeyEvent.KEY_RELEASED, KeyEvent.CHAR_UNDEFINED, "", code,
                false, false, false, false));
    }
}
