package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalSchedule.ALICE_MONDAY_MORNING;
import static seedu.address.testutil.TypicalSchedule.BENSON_MONDAY_OVERLAP;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.reminder.Reminder;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void addScheduledSession_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.addScheduledSession(null));
    }

    @Test
    public void addScheduledSession_validSession_appearsInScheduleList() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);
        modelManager.addScheduledSession(ALICE_MONDAY_MORNING);
        modelManager.addScheduledSession(BENSON_MONDAY_OVERLAP);

        assertEquals(List.of(ALICE_MONDAY_MORNING, BENSON_MONDAY_OVERLAP), modelManager.getScheduleList());
    }

    @Test
    public void getScheduleList_modifyList_throwsUnsupportedOperationException() {
        modelManager.addPerson(ALICE);
        modelManager.addScheduledSession(ALICE_MONDAY_MORNING);
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getScheduleList().remove(0));
    }

    @Test
    public void reminderOperations_addAccessDeleteAndPreventExternalMutation() {
        Reminder first = modelManager.addReminder("First", null);
        Reminder second = modelManager.addReminder("Second", null);

        assertEquals(List.of(first, second), modelManager.getReminderList());
        assertThrows(UnsupportedOperationException.class, () ->
                modelManager.getReminderList().add(new Reminder(3, "Third")));

        modelManager.deleteReminder(1);
        assertEquals(List.of(second), modelManager.getReminderList());
    }

    @Test
    public void undoRedo_reminder_restoresReminderState() {
        Reminder reminder = modelManager.addReminder("First", null);
        modelManager.save();
        modelManager.deleteReminder(1);
        modelManager.save();

        modelManager.undo();
        assertEquals(List.of(reminder), modelManager.getReminderList());
        modelManager.redo();
        assertEquals(List.of(), modelManager.getReminderList());
    }

    @Test
    public void undoRedo_scheduledSession_restoresSchedule() {
        modelManager.addPerson(ALICE);
        modelManager.save();

        modelManager.addScheduledSession(ALICE_MONDAY_MORNING);
        modelManager.save();

        modelManager.undo();
        assertEquals(List.of(), modelManager.getScheduleList());

        modelManager.redo();
        assertEquals(List.of(ALICE_MONDAY_MORNING), modelManager.getScheduleList());
    }

    @Test
    public void hasAndDeleteScheduledSession_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasScheduledSession(null));
        assertThrows(NullPointerException.class, () -> modelManager.deleteScheduledSession(null));
    }

    @Test
    public void deleteScheduledSession_existingSession_removesIt() {
        modelManager.addPerson(ALICE);
        modelManager.addScheduledSession(ALICE_MONDAY_MORNING);
        assertTrue(modelManager.hasScheduledSession(ALICE_MONDAY_MORNING));

        modelManager.deleteScheduledSession(ALICE_MONDAY_MORNING);

        assertFalse(modelManager.hasScheduledSession(ALICE_MONDAY_MORNING));
        assertEquals(List.of(), modelManager.getScheduleList());
    }

    @Test
    public void deleteScheduledSession_absentSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                modelManager.deleteScheduledSession(ALICE_MONDAY_MORNING));
    }

    @Test
    public void undo_afterDeletingSession_restoresSession() {
        modelManager.addPerson(ALICE);
        modelManager.addScheduledSession(ALICE_MONDAY_MORNING);
        modelManager.save();

        modelManager.deleteScheduledSession(ALICE_MONDAY_MORNING);
        modelManager.save();

        modelManager.undo();
        assertEquals(List.of(ALICE_MONDAY_MORNING), modelManager.getScheduleList());

        modelManager.redo();
        assertEquals(List.of(), modelManager.getScheduleList());
    }

    @Test
    public void save_validAddressBook_savesSnapshot() {
        modelManager.addPerson(ALICE);
        modelManager.save();

        modelManager.addPerson(BENSON);
        modelManager.save();

        modelManager.undo();

        assertTrue(modelManager.hasPerson(ALICE));
        assertFalse(modelManager.hasPerson(BENSON));
    }

    @Test
    public void undo_previousSnapshotExists_restoresPreviousState() {
        modelManager.addPerson(ALICE);
        modelManager.save();

        modelManager.addPerson(BENSON);
        modelManager.save();

        modelManager.undo();

        assertTrue(modelManager.hasPerson(ALICE));
        assertFalse(modelManager.hasPerson(BENSON));
    }

    @Test
    public void redo_previousUndoExists_restoresNextState() {
        modelManager.addPerson(ALICE);
        modelManager.save();

        modelManager.addPerson(BENSON);
        modelManager.save();

        modelManager.undo();
        modelManager.redo();

        assertTrue(modelManager.hasPerson(ALICE));
        assertTrue(modelManager.hasPerson(BENSON));
    }

    @Test
    public void undo_noPreviousSnapshot_doesNothing() {
        modelManager.undo();
        AddressBook expected = new AddressBook();
        expected.addPerson(ALICE);

        assertEquals(expected, new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void redo_noNextSnapshot_doesNothing() {
        modelManager.redo();

        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
