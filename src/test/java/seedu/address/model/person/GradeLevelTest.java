package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GradeLevelTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GradeLevel(null));
    }

    @Test
    public void constructor_invalidGradeLevel_throwsIllegalArgumentException() {
        String invalidGradeLevel = "";
        assertThrows(IllegalArgumentException.class, () -> new GradeLevel(invalidGradeLevel));
    }

    @Test
    public void isValidGradeLevel() {
        // null grade level
        assertThrows(NullPointerException.class, () -> GradeLevel.isValidGradeLevel(null));

        // invalid grade levels
        assertFalse(GradeLevel.isValidGradeLevel("")); // empty string
        assertFalse(GradeLevel.isValidGradeLevel(" ")); // spaces only
        assertFalse(GradeLevel.isValidGradeLevel(" Sec 3")); // leading space

        // valid grade levels
        assertTrue(GradeLevel.isValidGradeLevel("Sec 3"));
        assertTrue(GradeLevel.isValidGradeLevel("P6"));
        assertTrue(GradeLevel.isValidGradeLevel("JC1 (H2 track)")); // free text
    }

    @Test
    public void none_isNotPresent() {
        assertFalse(GradeLevel.NONE.isPresent());
        assertEquals("", GradeLevel.NONE.value);
        assertTrue(new GradeLevel("P6").isPresent());
    }

    @Test
    public void equals() {
        GradeLevel gradeLevel = new GradeLevel("Sec 3");

        // same values -> returns true
        assertTrue(gradeLevel.equals(new GradeLevel("Sec 3")));

        // same object -> returns true
        assertTrue(gradeLevel.equals(gradeLevel));

        // null -> returns false
        assertFalse(gradeLevel.equals(null));

        // different types -> returns false
        assertFalse(gradeLevel.equals(5.0f));

        // different values -> returns false
        assertFalse(gradeLevel.equals(new GradeLevel("P6")));

        // none vs specified -> returns false
        assertFalse(gradeLevel.equals(GradeLevel.NONE));
    }
}
