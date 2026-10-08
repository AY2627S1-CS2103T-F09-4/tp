package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PhotoPath;

/**
 * Adds or replaces the photo of a person identified using its displayed index from the address book.
 */
public class AddPhotoCommand extends Command {

    public static final String COMMAND_WORD = "add-photo";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds or replaces the photo of the student identified by the index number used in the displayed "
            + "student list.\n"
            + "Parameters: INDEX f/FILE_PATH\n"
            + "Example: " + COMMAND_WORD + " 3 f/./photos/john_tan.png";

    public static final String MESSAGE_ADD_PHOTO_SUCCESS = "Added photo to student: %1$s";
    public static final String MESSAGE_PHOTO_FILE_MISSING = "Photo file does not exist: %1$s";
    public static final String MESSAGE_PHOTO_FILE_UNREADABLE = "Photo file cannot be read: %1$s";
    public static final String MESSAGE_PHOTO_FILE_UNSUPPORTED =
            "Photo file must be a JPG, JPEG, or PNG image: %1$s";
    public static final String MESSAGE_PHOTO_FILE_TOO_LARGE = "Photo file must not exceed 5 MB: %1$s";
    public static final String MESSAGE_PHOTO_FILE_INVALID = "Photo file cannot be loaded as an image: %1$s";

    private static final long MAX_PHOTO_FILE_SIZE_BYTES = 5L * 1024 * 1024;

    private final Index targetIndex;
    private final Path photoFilePath;

    /**
     * Creates an AddPhotoCommand to add or replace a student's photo.
     */
    public AddPhotoCommand(Index targetIndex, Path photoFilePath) {
        requireNonNull(targetIndex);
        requireNonNull(photoFilePath);
        this.targetIndex = targetIndex;
        this.photoFilePath = photoFilePath;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        validatePhotoFile(photoFilePath);

        Person personToUpdate = lastShownList.get(targetIndex.getZeroBased());
        Person updatedPerson = new Person(personToUpdate.getName(), personToUpdate.getPhone(),
                personToUpdate.getEmail(), personToUpdate.getAddress(), personToUpdate.getSubject(),
                personToUpdate.getGradeLevel(), new PhotoPath(photoFilePath.toString()), personToUpdate.getTags());

        model.setPerson(personToUpdate, updatedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_ADD_PHOTO_SUCCESS, Messages.format(updatedPerson)));
    }

    private static void validatePhotoFile(Path photoFilePath) throws CommandException {
        if (!PhotoPath.hasSupportedExtension(photoFilePath.toString())) {
            throw new CommandException(String.format(MESSAGE_PHOTO_FILE_UNSUPPORTED, photoFilePath));
        }
        if (!Files.exists(photoFilePath) || !Files.isRegularFile(photoFilePath)) {
            throw new CommandException(String.format(MESSAGE_PHOTO_FILE_MISSING, photoFilePath));
        }
        if (!Files.isReadable(photoFilePath)) {
            throw new CommandException(String.format(MESSAGE_PHOTO_FILE_UNREADABLE, photoFilePath));
        }
        try {
            if (Files.size(photoFilePath) > MAX_PHOTO_FILE_SIZE_BYTES) {
                throw new CommandException(String.format(MESSAGE_PHOTO_FILE_TOO_LARGE, photoFilePath));
            }
            BufferedImage image = ImageIO.read(photoFilePath.toFile());
            if (image == null) {
                throw new CommandException(String.format(MESSAGE_PHOTO_FILE_INVALID, photoFilePath));
            }
        } catch (IOException ioe) {
            throw new CommandException(String.format(MESSAGE_PHOTO_FILE_UNREADABLE, photoFilePath), ioe);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddPhotoCommand otherAddPhotoCommand)) {
            return false;
        }

        return targetIndex.equals(otherAddPhotoCommand.targetIndex)
                && photoFilePath.equals(otherAddPhotoCommand.photoFilePath);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("photoFilePath", photoFilePath)
                .toString();
    }
}
