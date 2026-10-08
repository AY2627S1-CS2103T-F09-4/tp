package seedu.address.ui;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.logic.Logic;
import seedu.address.model.person.ParentContact;

/** Displays parent contacts in the Contacts tab. */
public class ParentListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logic logic;
    private final ListView<ParentContact> listView = new ListView<>();

    /**
     * Creates a panel displaying parent contacts.
     */
    public ParentListPanel(
            ObservableList<ParentContact> contacts,
            Logic logic,
            Consumer<String> selectStudent) {
        super(FXML);
        this.logic = logic;
        listView.setItems(contacts);
        getRoot().getChildrenUnmodifiable();
        javafx.scene.layout.VBox root = (javafx.scene.layout.VBox) getRoot();
        root.getChildren().setAll(listView);
        listView.setCellFactory(
                view -> new ListCell<>() {
                    @Override
                    protected void updateItem(ParentContact contact, boolean empty) {
                        super.updateItem(contact, empty);
                        Runnable selectLinkedStudent = () -> selectStudent.accept(contact.getLinkedStudentId());
                        setGraphic(empty || contact == null
                                ? null
                                : new ParentContactCard(
                                        contact,
                                        getIndex() + 1,
                                        logic,
                                        selectLinkedStudent).getRoot());
                        setText(null);
                    }
                });
    }
}
