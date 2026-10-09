package seedu.address.model.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void constructor_nullTasks_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TaskList(null));
    }

    @Test
    public void constructor_nullTask_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TaskList(java.util.Arrays.asList((Task) null)));
    }

    @Test
    public void constructor_copiesInputList() {
        List<Task> source = new ArrayList<>(List.of(new Task("do homework")));
        TaskList taskList = new TaskList(source);

        source.add(new Task("revise notes"));

        assertEquals(List.of(new Task("do homework")), taskList.getTasks());
    }

    @Test
    public void getTasks_unmodifiable() {
        TaskList taskList = new TaskList(List.of(new Task("do homework")));

        assertThrows(UnsupportedOperationException.class, () -> taskList.getTasks().add(new Task("revise notes")));
    }

    @Test
    public void withTask_appendsAndPreservesOriginal() {
        TaskList original = new TaskList(List.of(new Task("do homework")));
        TaskList updated = original.withTask(new Task("revise notes"));

        assertEquals(List.of(new Task("do homework")), original.getTasks());
        assertEquals(List.of(new Task("do homework"), new Task("revise notes")), updated.getTasks());
    }

    @Test
    public void withTask_allowsRepeatedTasksAndKeepsOrder() {
        Task repeated = new Task("do homework");
        TaskList taskList = new TaskList().withTask(repeated).withTask(new Task("revise notes")).withTask(repeated);

        assertEquals(List.of(repeated, new Task("revise notes"), repeated), taskList.getTasks());
    }

    @Test
    public void equals_orderMatters() {
        TaskList first = new TaskList(List.of(new Task("do homework"), new Task("revise notes")));
        TaskList same = new TaskList(List.of(new Task("do homework"), new Task("revise notes")));
        TaskList differentOrder = new TaskList(List.of(new Task("revise notes"), new Task("do homework")));

        assertEquals(first, same);
        assertFalse(first.equals(differentOrder));
    }
}
