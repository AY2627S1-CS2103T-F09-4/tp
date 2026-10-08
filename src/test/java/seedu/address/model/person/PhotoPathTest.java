package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhotoPathTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PhotoPath(null));
    }

    @Test
    public void isPresent() {
        assertFalse(PhotoPath.NONE.isPresent());
        assertFalse(new PhotoPath("   ").isPresent());
        assertTrue(new PhotoPath("student.png").isPresent());
    }

    @Test
    public void hasSupportedExtension() {
        assertThrows(NullPointerException.class, () -> PhotoPath.hasSupportedExtension(null));

        assertTrue(PhotoPath.hasSupportedExtension("student.jpg"));
        assertTrue(PhotoPath.hasSupportedExtension("student.jpeg"));
        assertTrue(PhotoPath.hasSupportedExtension("student.png"));
        assertTrue(PhotoPath.hasSupportedExtension("student.JPG"));

        assertFalse(PhotoPath.hasSupportedExtension("student.gif"));
        assertFalse(PhotoPath.hasSupportedExtension("student.png.txt"));
        assertFalse(PhotoPath.hasSupportedExtension("student"));
    }
}
