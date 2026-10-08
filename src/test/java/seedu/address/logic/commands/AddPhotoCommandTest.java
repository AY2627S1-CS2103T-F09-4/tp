package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PhotoPath;

public class AddPhotoCommandTest {

    @TempDir
    public Path testFolder;

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws IOException {
        Path photoPath = createImageFile("amy.png", "png");
        AddPhotoCommand addPhotoCommand = new AddPhotoCommand(INDEX_FIRST_PERSON, photoPath);

        Person personToUpdate = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person updatedPerson = createPersonWithPhoto(personToUpdate, photoPath);
        String expectedMessage = String.format(AddPhotoCommand.MESSAGE_ADD_PHOTO_SUCCESS,
                Messages.format(updatedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToUpdate, updatedPerson);

        assertCommandSuccess(addPhotoCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws IOException {
        Path photoPath = createImageFile("amy.jpg", "jpg");
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        AddPhotoCommand addPhotoCommand = new AddPhotoCommand(INDEX_FIRST_PERSON, photoPath);

        Person personToUpdate = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person updatedPerson = createPersonWithPhoto(personToUpdate, photoPath);
        String expectedMessage = String.format(AddPhotoCommand.MESSAGE_ADD_PHOTO_SUCCESS,
                Messages.format(updatedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToUpdate, updatedPerson);

        assertCommandSuccess(addPhotoCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndex_failure() throws IOException {
        Path photoPath = createImageFile("amy.png", "png");
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AddPhotoCommand addPhotoCommand = new AddPhotoCommand(outOfBoundIndex, photoPath);

        assertCommandFailure(addPhotoCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_missingFile_failure() {
        Path missingPhotoPath = testFolder.resolve("missing.png");
        AddPhotoCommand addPhotoCommand = new AddPhotoCommand(INDEX_FIRST_PERSON, missingPhotoPath);

        assertCommandFailure(addPhotoCommand, model,
                String.format(AddPhotoCommand.MESSAGE_PHOTO_FILE_MISSING, missingPhotoPath));
    }

    @Test
    public void execute_unsupportedFileExtension_failure() throws IOException {
        Path textFilePath = createPhotoFile("amy.gif", 10);
        AddPhotoCommand addPhotoCommand = new AddPhotoCommand(INDEX_FIRST_PERSON, textFilePath);

        assertCommandFailure(addPhotoCommand, model,
                String.format(AddPhotoCommand.MESSAGE_PHOTO_FILE_UNSUPPORTED, textFilePath));
    }

    @Test
    public void execute_invalidImageFile_failure() throws IOException {
        Path invalidPhotoPath = createPhotoFile("amy.png", 10);
        AddPhotoCommand addPhotoCommand = new AddPhotoCommand(INDEX_FIRST_PERSON, invalidPhotoPath);

        assertCommandFailure(addPhotoCommand, model,
                String.format(AddPhotoCommand.MESSAGE_PHOTO_FILE_INVALID, invalidPhotoPath));
    }

    @Test
    public void execute_fileTooLarge_failure() throws IOException {
        Path largePhotoPath = createPhotoFile("large.jpeg", 5 * 1024 * 1024 + 1);
        AddPhotoCommand addPhotoCommand = new AddPhotoCommand(INDEX_FIRST_PERSON, largePhotoPath);

        assertCommandFailure(addPhotoCommand, model,
                String.format(AddPhotoCommand.MESSAGE_PHOTO_FILE_TOO_LARGE, largePhotoPath));
    }

    @Test
    public void equals() throws IOException {
        Path firstPhotoPath = createImageFile("amy.png", "png");
        Path secondPhotoPath = createImageFile("bob.png", "png");
        AddPhotoCommand addFirstPhotoCommand = new AddPhotoCommand(INDEX_FIRST_PERSON, firstPhotoPath);
        AddPhotoCommand addFirstPhotoCommandCopy = new AddPhotoCommand(INDEX_FIRST_PERSON, firstPhotoPath);
        AddPhotoCommand addSecondPhotoCommand = new AddPhotoCommand(INDEX_SECOND_PERSON, firstPhotoPath);
        AddPhotoCommand addDifferentPhotoCommand = new AddPhotoCommand(INDEX_FIRST_PERSON, secondPhotoPath);

        assertTrue(addFirstPhotoCommand.equals(addFirstPhotoCommand));
        assertTrue(addFirstPhotoCommand.equals(addFirstPhotoCommandCopy));
        assertFalse(addFirstPhotoCommand.equals(addSecondPhotoCommand));
        assertFalse(addFirstPhotoCommand.equals(addDifferentPhotoCommand));
        assertFalse(addFirstPhotoCommand.equals(null));
        assertFalse(addFirstPhotoCommand.equals(1));
    }

    @Test
    public void toStringMethod() throws IOException {
        Path photoPath = createImageFile("amy.png", "png");
        AddPhotoCommand addPhotoCommand = new AddPhotoCommand(INDEX_FIRST_PERSON, photoPath);
        String expected = AddPhotoCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", photoFilePath=" + photoPath + "}";
        assertEquals(expected, addPhotoCommand.toString());
    }

    private Path createPhotoFile(String fileName, int sizeInBytes) throws IOException {
        Path photoPath = testFolder.resolve(fileName);
        Files.write(photoPath, new byte[sizeInBytes]);
        return photoPath;
    }

    private Path createImageFile(String fileName, String formatName) throws IOException {
        Path photoPath = testFolder.resolve(fileName);
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, formatName, photoPath.toFile());
        return photoPath;
    }

    private static Person createPersonWithPhoto(Person person, Path photoPath) {
        return new Person(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
                person.getSubject(), person.getGradeLevel(), new PhotoPath(photoPath.toString()), person.getTags());
    }
}
