package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskStatus;

public class JsonAdaptedTaskTest {

    @Test
    public void roundTrip_preservesDescriptionAndStatus() throws Exception {
        for (TaskStatus status : TaskStatus.values()) {
            Task task = new Task("do homework", status);
            String json = JsonUtil.toJsonString(new JsonAdaptedTask(task));
            Task readBack = JsonUtil.fromJsonString(json, JsonAdaptedTask.class).toModelType();
            assertEquals(task, readBack);
        }
    }

    @Test
    public void serialize_writesObjectWithStatusText() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedTask(new Task("do homework", TaskStatus.DONE)));
        assertTrue(json.contains("\"description\" : \"do homework\""));
        assertTrue(json.contains("\"status\" : \"done\""));
    }

    @Test
    public void deserialize_legacyPlainString_loadsAsNotDone() throws Exception {
        Task task = JsonUtil.fromJsonString("\"do homework\"", JsonAdaptedTask.class).toModelType();
        assertEquals(new Task("do homework", TaskStatus.NOT_DONE), task);
    }

    @Test
    public void toModelType_missingOrEmptyStatus_loadsAsNotDone() throws Exception {
        assertEquals(new Task("do homework"), new JsonAdaptedTask("do homework", null).toModelType());
        assertEquals(new Task("do homework"), new JsonAdaptedTask("do homework", "").toModelType());
    }

    @Test
    public void toModelType_invalidStatus_throwsIllegalValueException() {
        JsonAdaptedTask task = new JsonAdaptedTask("do homework", "finished");
        assertThrows(IllegalValueException.class, TaskStatus.MESSAGE_CONSTRAINTS, task::toModelType);
    }

    @Test
    public void toModelType_invalidDescription_throwsIllegalValueException() {
        JsonAdaptedTask task = new JsonAdaptedTask(" ", "done");
        assertThrows(IllegalValueException.class, Task.MESSAGE_CONSTRAINTS, task::toModelType);
    }
}
