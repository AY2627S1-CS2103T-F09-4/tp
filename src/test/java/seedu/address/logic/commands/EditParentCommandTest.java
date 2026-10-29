package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.EditParentCommandParser;
import seedu.address.model.ModelManager;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.SnapshotManager;
import seedu.address.testutil.ParentContactBuilder;
import seedu.address.testutil.PersonBuilder;

public class EditParentCommandTest {
    @TempDir
    public Path tempDir;

    private final EditParentCommandParser parser = new EditParentCommandParser();
    private ModelManager model;
    private ParentContact parent;
    private Person firstStudent;
    private Person secondStudent;

    @BeforeEach
    public void setUp() {
        SnapshotManager.reset();
        model = new ModelManager();
        parent = new ParentContactBuilder().withEmail(null).withPhotoPath("photos/parent.png")
                .withRequirements("No food", "Call before lessons", "Send weekly updates").build();
        firstStudent = new PersonBuilder().withName("First Student").build();
        secondStudent = new PersonBuilder().withName("Second Student").withPhone("91234567").build();
        model.addParentContact(parent);
        model.addPerson(firstStudent);
        model.addPerson(secondStudent);
    }

    @Test
    public void execute_existingFields_preservesUneditedData() throws Exception {
        for (String input : List.of("1 n/New Name", "1 p/98765432", "1 e/new@example.com")) {
            parser.parse(input).execute(model);
        }
        ParentContact edited = current();
        assertEquals("New Name", edited.getName().fullName);
        assertEquals("98765432", edited.getPhone().value);
        assertEquals("new@example.com", edited.getEmail().value);
        assertEquals(parent.getRequirements(), edited.getRequirements());
        assertEquals(parent.getId(), edited.getId());
        assertEquals(parent.getPhotoPath(), edited.getPhotoPath());
    }

    @Test
    public void execute_link_addReplaceRetainAndClear() throws Exception {
        parser.parse("1 link/1").execute(model);
        assertLink(firstStudent);
        parser.parse("1 link/1").execute(model);
        assertLink(firstStudent);
        parser.parse("1 link/2").execute(model);
        assertLink(secondStudent);
        parser.parse("1 p/98765432").execute(model);
        assertLink(secondStudent);
        parser.parse("1 link/").execute(model);
        assertNull(current().getLinkedStudentId());
        assertNull(current().getLinkedStudentName());
        assertEquals(parent.getRequirements(), current().getRequirements());
    }

    @Test
    public void execute_requirements_replaceDeleteSelectedAndClear() throws Exception {
        parser.parse("1 r/No food r/Send weekly updates").execute(model);
        assertEquals(List.of("No food", "Send weekly updates"), current().getRequirements());
        parser.parse("1 r/New requirement").execute(model);
        assertEquals(List.of("New requirement"), current().getRequirements());
        parser.parse("1 r/").execute(model);
        assertEquals(List.of(), current().getRequirements());
        assertEquals(parent.getPhotoPath(), current().getPhotoPath());
    }

    @Test
    public void execute_filteredLists_usesDisplayedIndicesAndChecksHiddenParents() throws Exception {
        model.updateFilteredPersonList(student -> student.equals(secondStudent));
        parser.parse("1 link/1").execute(model);
        assertLink(secondStudent);
        ParentContact other = new ParentContactBuilder().withId("other").withName("Other Parent")
                .withLinkedStudentId(firstStudent.getStudentId()).build();
        model.addParentContact(other);
        model.updateParentList(contact -> contact.getId().equals(parent.getId()));
        model.updateFilteredPersonList(student -> student.equals(firstStudent));
        assertFailure("1 link/1 r/Changed", AddContactCommand.MESSAGE_STUDENT_ALREADY_LINKED);
    }

    @Test
    public void execute_invalidEdit_reportsReasonAndUsageWithoutMutation() {
        assertFailure("2 n/New Name", Messages.MESSAGE_INVALID_PARENT_DISPLAYED_INDEX);
        assertFailure("1 n/New Name link/3 r/Changed", Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        ParentContact other = new ParentContactBuilder().withId("other").withName("Other Parent")
                .withPhone("98765432").build();
        model.addParentContact(other);
        assertFailure("1 n/Other Parent p/98765432 r/Changed", AddContactCommand.MESSAGE_DUPLICATE_PARENT);
    }

    @Test
    public void execute_combinedEdit_roundTripsAndSupportsUndoRedo() throws Exception {
        model.save("Before edit");
        parser.parse("1 n/New Name link/2 r/Keep this r/Keep that").execute(model);
        ParentContact edited = current();
        assertLink(secondStudent);
        assertEquals(List.of("Keep this", "Keep that"), edited.getRequirements());
        assertNull(edited.getEmail());
        assertEquals(parent.getPhotoPath(), edited.getPhotoPath());
        model.save("Edit");
        model.undo();
        assertEquals(parent, current());
        model.redo();
        assertEquals(edited, current());

        Path file = tempDir.resolve("parents.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.saveAddressBook(model.getAddressBook());
        ParentContact reloaded = storage.readAddressBook().orElseThrow().getParentContactList().getFirst();
        assertEquals(edited, reloaded);
        assertEquals(edited.getLinkedStudentName(), reloaded.getLinkedStudentName());

        parser.parse("1 link/ r/").execute(model);
        ParentContact cleared = current();
        model.save("Clear");
        model.undo();
        assertEquals(edited, current());
        model.redo();
        assertEquals(cleared, current());
        storage.saveAddressBook(model.getAddressBook());
        assertEquals(cleared, storage.readAddressBook().orElseThrow().getParentContactList().getFirst());
    }

    private ParentContact current() {
        return model.getParentContactList().getFirst();
    }

    private void assertLink(Person student) {
        assertEquals(student.getStudentId(), current().getLinkedStudentId());
        assertEquals(student.getName().fullName, current().getLinkedStudentName());
    }

    private void assertFailure(String input, String reason) {
        List<ParentContact> before = List.copyOf(model.getAddressBook().getParentContactList());
        CommandException error = assertThrows(CommandException.class, () -> parser.parse(input).execute(model));
        assertEquals(EditParentCommand.withUsage(reason), error.getMessage());
        assertEquals(before, model.getAddressBook().getParentContactList());
    }
}
