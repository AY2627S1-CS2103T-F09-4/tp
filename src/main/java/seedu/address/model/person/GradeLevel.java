package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's (optional) grade level in the address book, e.g. "Sec 3" or "P6".
 * An empty value means that no grade level was specified.
 * Guarantees: immutable; is valid as declared in {@link #isValidGradeLevel(String)}
 */
public class GradeLevel {

    public static final String MESSAGE_CONSTRAINTS = "Grade level should not be blank if specified.";

    /*
     * The first character of the grade level must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    /** Used when a person has no grade level. */
    public static final GradeLevel NONE = new GradeLevel();

    public final String value;

    private GradeLevel() {
        value = "";
    }

    /**
     * Constructs a {@code GradeLevel}.
     *
     * @param gradeLevel A valid grade level.
     */
    public GradeLevel(String gradeLevel) {
        requireNonNull(gradeLevel);
        checkArgument(isValidGradeLevel(gradeLevel), MESSAGE_CONSTRAINTS);
        value = gradeLevel;
    }

    /**
     * Returns true if a given string is a valid grade level.
     */
    public static boolean isValidGradeLevel(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a grade level was specified.
     */
    public boolean isPresent() {
        return !value.isEmpty();
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

        // instanceof handles nulls
        if (!(other instanceof GradeLevel otherGradeLevel)) {
            return false;
        }

        return value.equals(otherGradeLevel.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
