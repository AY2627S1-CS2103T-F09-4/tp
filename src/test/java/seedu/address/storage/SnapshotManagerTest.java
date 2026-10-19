package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.testutil.PersonBuilder;

class SnapshotManagerTest {

    private SnapshotManager snapshotManager;
    private AddressBook initialAddressBook = new AddressBook();

    private void populate(AddressBook addressBook) {
        addressBook.addPerson(new PersonBuilder().withName("Alice Pauline")
                .withAddress("123, Jurong West Ave 6, #08-111").withEmail("alice@example.com")
                .withPhone("94351253").withGradeLevel("2")
                .withTags("friends").build());
    }

    @BeforeEach
    void setUp() {
        snapshotManager = new SnapshotManager();
        SnapshotManager.reset();
        populate(initialAddressBook);
        snapshotManager.saveSnapshot(initialAddressBook);
    }

    @Test
    void undo_withNoPreviousSnapshot_returnsNull() {
        assertNull(snapshotManager.undo());
    }

    @Test
    void redo_withNoNextSnapshot_returnsNull() {
        assertNull(snapshotManager.redo());
    }

    @Test
    void saveSnapshot_thenUndo_returnsPreviousSnapshot() {
        AddressBook modifiedAddressBook = initialAddressBook.copy();

        // Change the address book here using your existing model methods.
        // For this basic test, we can use a different initial state instead.
        AddressBook emptyAddressBook = new AddressBook();

        snapshotManager.saveSnapshot(emptyAddressBook);

        AddressBook result = snapshotManager.undo();

        assertNotNull(result);
        assertEquals(initialAddressBook, result);
    }

    @Test
    void undo_thenRedo_returnsLaterSnapshot() {
        AddressBook emptyAddressBook = new AddressBook();
        snapshotManager.saveSnapshot(emptyAddressBook);

        AddressBook undone = snapshotManager.undo();
        assertEquals(initialAddressBook, undone);

        AddressBook redone = snapshotManager.redo();

        assertNotNull(redone);
        assertEquals(emptyAddressBook, redone);
    }

    @Test
    void undo_thenSaveSnapshot_clearsRedoHistory() {
        AddressBook emptyAddressBook = new AddressBook();
        snapshotManager.saveSnapshot(emptyAddressBook);

        snapshotManager.undo();

        // Create a new branch of history.
        snapshotManager.saveSnapshot(new AddressBook());

        assertFalse(snapshotManager.canRedo());
    }

    @Test
    void canUndoAndCanRedo_trackHistoryCorrectly() {
        assertFalse(snapshotManager.canUndo());
        assertFalse(snapshotManager.canRedo());

        snapshotManager.saveSnapshot(new AddressBook());

        assertTrue(snapshotManager.canUndo());
        assertFalse(snapshotManager.canRedo());

        snapshotManager.undo();

        assertFalse(snapshotManager.canUndo());
        assertTrue(snapshotManager.canRedo());

        snapshotManager.redo();

        assertTrue(snapshotManager.canUndo());
        assertFalse(snapshotManager.canRedo());
    }
}
