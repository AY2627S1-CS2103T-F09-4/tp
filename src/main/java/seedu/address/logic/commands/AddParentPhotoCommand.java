package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.PhotoPath;

/** Adds a photo to a parent contact. */
public class AddParentPhotoCommand extends Command {
    public static final String COMMAND_WORD = "add-parent-photo";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a photo to a parent. Parameters: "
            + "INDEX f/FILE_PATH";
    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private final Index index;
    private final Path path;

    /**
     * Creates a command to add a parent contact photo.
     */
    public AddParentPhotoCommand(Index index, Path path) {
        this.index = requireNonNull(index);
        this.path = requireNonNull(path);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<ParentContact> contacts = model.getParentContactList();
        if (index.getZeroBased() >= contacts.size()) {
            throw new CommandException("The parent index provided is invalid.");
        }
        validate(path);
        ParentContact old = contacts.get(index.getZeroBased());
        ParentContact updated = new ParentContact(
                old.getId(), old.getName(), old.getPhone(), old.getEmail(), old.getLinkedStudentId(),
                old.getRequirements(), new PhotoPath(path.toString()));
        model.setParentContact(old, updated);
        return new CommandResult("Added photo to parent: " + updated.getName());
    }

    private static void validate(Path path) throws CommandException {
        if (!PhotoPath.hasSupportedExtension(path.toString())) {
            throw new CommandException("Photo file must be a JPG, JPEG, or PNG image: " + path);
        }
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new CommandException("Photo file does not exist: " + path);
        }
        try {
            if (Files.size(path) > MAX_SIZE) {
                throw new CommandException("Photo file must not exceed 5 MB: " + path);
            }
            BufferedImage image = ImageIO.read(path.toFile());
            if (image == null) {
                throw new CommandException("Photo file cannot be loaded as an image: " + path);
            }
        } catch (IOException e) {
            throw new CommandException("Photo file cannot be read: " + path, e);
        }
    }
}
