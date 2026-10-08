package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Represents a Person's photo path in the address book.
 * Guarantees: immutable.
 */
public class PhotoPath {

    public static final PhotoPath NONE = new PhotoPath("");

    public static final String MESSAGE_CONSTRAINTS =
            "Photo path should point to a JPG, JPEG, or PNG image file.";

    private static final String JPG_EXTENSION = ".jpg";
    private static final String JPEG_EXTENSION = ".jpeg";
    private static final String PNG_EXTENSION = ".png";

    public final String value;

    /**
     * Constructs a {@code PhotoPath}.
     */
    public PhotoPath(String photoPath) {
        requireNonNull(photoPath);
        value = photoPath.trim();
    }

    /**
     * Returns true if a photo path has been specified.
     */
    public boolean isPresent() {
        return !value.isEmpty();
    }

    /**
     * Returns true if the given string has a supported image file extension.
     */
    public static boolean hasSupportedExtension(String photoPath) {
        requireNonNull(photoPath);
        String lowerCasePhotoPath = photoPath.toLowerCase(Locale.ROOT);
        return lowerCasePhotoPath.endsWith(JPG_EXTENSION)
                || lowerCasePhotoPath.endsWith(JPEG_EXTENSION)
                || lowerCasePhotoPath.endsWith(PNG_EXTENSION);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PhotoPath otherPhotoPath)) {
            return false;
        }

        return value.equals(otherPhotoPath.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
