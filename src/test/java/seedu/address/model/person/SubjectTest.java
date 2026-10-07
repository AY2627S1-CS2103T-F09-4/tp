package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SubjectTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Subject(null));
    }

    @Test
    public void constructor_invalidSubject_throwsIllegalArgumentException() {
        String invalidSubject = "";
        assertThrows(IllegalArgumentException.class, () -> new Subject(invalidSubject));
    }

    @Test
    public void isValidSubject() {
        // null subject
        assertThrows(NullPointerException.class, () -> Subject.isValidSubject(null));

        // invalid subjects
        assertFalse(Subject.isValidSubject("")); // empty string
        assertFalse(Subject.isValidSubject(" ")); // spaces only
        assertFalse(Subject.isValidSubject(" Mathematics")); // leading space

        // valid subjects
        assertTrue(Subject.isValidSubject("Mathematics"));
        assertTrue(Subject.isValidSubject("Additional Mathematics")); // with spaces
        assertTrue(Subject.isValidSubject("H2 Chemistry")); // with digits
        assertTrue(Subject.isValidSubject("-")); // one character
    }

    @Test
    public void equals() {
        Subject subject = new Subject("Mathematics");

        // same values -> returns true
        assertTrue(subject.equals(new Subject("Mathematics")));

        // same object -> returns true
        assertTrue(subject.equals(subject));

        // null -> returns false
        assertFalse(subject.equals(null));

        // different types -> returns false
        assertFalse(subject.equals(5.0f));

        // different values -> returns false
        assertFalse(subject.equals(new Subject("English")));
    }
}
