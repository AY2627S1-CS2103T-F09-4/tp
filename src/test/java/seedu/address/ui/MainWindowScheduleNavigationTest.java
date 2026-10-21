package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainWindowScheduleNavigationTest {
    @TempDir
    public Path folder;

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
    }

    @Test
    public void scheduleCommands_selectExistingScheduleTab_beforeGenericListRouting() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(folder.resolve("book.json")),
                    new JsonUserPrefsStorage(folder.resolve("prefs.json")));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, new LogicManager(new ModelManager(), storage),
                    folder.resolve("book.json"));
            TabPane tabs = (TabPane) stage.getScene().lookup("#recordTabs");
            window.selectTabForCommand("list-parents");
            assertEquals("Parents", tabs.getSelectionModel().getSelectedItem().getText());
            window.selectTabForCommand("  list-schedule  ");
            assertEquals("Schedule", tabs.getSelectionModel().getSelectedItem().getText());
            window.selectTabForCommand("list");
            assertEquals("Students", tabs.getSelectionModel().getSelectedItem().getText());
            window.selectTabForCommand("mark-attendance 1 date/2026-10-19 status/completed");
            assertEquals("Schedule", tabs.getSelectionModel().getSelectedItem().getText());
            stage.close();
        });
    }
}
