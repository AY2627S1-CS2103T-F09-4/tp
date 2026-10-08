package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.logic.Logic;
import seedu.address.model.person.ParentContact;
import java.util.function.Consumer;

/** Displays parent contacts in the Contacts tab. */
public class ParentListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logic logic;
    private final ListView<ParentContact> listView = new ListView<>();

    public ParentListPanel(ObservableList<ParentContact> contacts, Logic logic, Consumer<String> selectStudent) {
        super(FXML);
        this.logic = logic;
        listView.setItems(contacts);
        getRoot().getChildrenUnmodifiable();
        ((javafx.scene.layout.VBox) getRoot()).getChildren().setAll(listView);
        listView.setCellFactory(view -> new ListCell<>() {
            @Override protected void updateItem(ParentContact contact, boolean empty) {
                super.updateItem(contact, empty);
                setGraphic(empty || contact == null ? null
                        : new ParentContactCard(contact, getIndex() + 1, logic,
                                () -> selectStudent.accept(contact.getLinkedStudentId())).getRoot());
                setText(null);
            }
        });
    }
}
