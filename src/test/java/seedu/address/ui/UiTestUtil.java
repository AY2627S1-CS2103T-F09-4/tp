package seedu.address.ui;

import java.lang.reflect.Field;

/**
 * Helper methods for UI tests.
 */
final class UiTestUtil {

    private UiTestUtil() {}

    /**
     * Returns the value of the private field {@code fieldName} of {@code owner},
     * such as an FXML-injected control.
     */
    @SuppressWarnings("unchecked")
    static <T> T fieldOf(Object owner, String fieldName) throws ReflectiveOperationException {
        Field field = owner.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(owner);
    }
}
