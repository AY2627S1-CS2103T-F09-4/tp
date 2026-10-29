package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.ParentContactBuilder;

public class MainWindowViewChildTest {
    @TempDir
    public Path folder;

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
        FxTestSupport.runOnFxThread(() -> Platform.setImplicitExit(false));
    }

    @Test
    public void viewChild_fromParentsTab_selectsLinkedStudentOnStudentsTab() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            ModelManager model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
            model.addParentContact(
                    new ParentContactBuilder().withLinkedStudentId(BENSON.getStudentId()).build());
            StorageManager storage = new StorageManager(
                    new JsonAddressBookStorage(folder.resolve("book.json")),
                    new JsonUserPrefsStorage(folder.resolve("prefs.json")));
            Stage stage = new Stage();
            new MainWindow(stage, new LogicManager(model, storage), folder.resolve("book.json"))
                    .fillInnerParts();
            stage.show();
            try {
                TabPane tabs = (TabPane) stage.getScene().lookup("#recordTabs");
                tabs.getSelectionModel().select(1);
                TextField commandBox = (TextField) stage.getScene().lookup("#commandTextField");

                commandBox.setText("view-child 1");
                Event.fireEvent(commandBox, new KeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED, "",
                        KeyCode.ENTER, false, false, false, false));

                assertEquals("Students", tabs.getSelectionModel().getSelectedItem().getText());
                // The Parents tab reuses the student list's layout, so find the list inside the Students tab.
                ListView<?> students = (ListView<?>) stage.getScene().lookup("#personListPanelPlaceholder")
                        .lookup("#personListView");
                assertEquals(BENSON, students.getSelectionModel().getSelectedItem());
            } finally {
                stage.close();
            }
        });
    }
}
