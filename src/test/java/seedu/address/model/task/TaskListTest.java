package seedu.address.model.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;

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
    public void size_returnsNumberOfTasks() {
        assertEquals(0, new TaskList().size());
        assertEquals(2, new TaskList(List.of(new Task("do homework"), new Task("revise notes"))).size());
    }

    @Test
    public void getTask_returnsTaskAtIndexAndRejectsOutOfRange() {
        TaskList taskList = new TaskList(List.of(new Task("do homework"), new Task("revise notes")));

        assertEquals(new Task("do homework"), taskList.getTask(Index.fromOneBased(1)));
        assertEquals(new Task("revise notes"), taskList.getTask(Index.fromOneBased(2)));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.getTask(Index.fromOneBased(3)));
    }

    @Test
    public void withTaskStatus_replacesOnlyTargetTaskAndPreservesOriginal() {
        TaskList original = new TaskList(List.of(new Task("do homework"), new Task("revise notes")));
        TaskList updated = original.withTaskStatus(Index.fromOneBased(2), TaskStatus.DONE);

        assertEquals(List.of(new Task("do homework"), new Task("revise notes")), original.getTasks());
        assertEquals(List.of(new Task("do homework"), new Task("revise notes", TaskStatus.DONE)),
                updated.getTasks());
    }

    @Test
    public void withoutTask_removesOnlyTargetTaskAndPreservesOriginal() {
        TaskList original = new TaskList(List.of(new Task("a"), new Task("b"), new Task("c")));
        TaskList updated = original.withoutTask(Index.fromOneBased(2));

        assertEquals(List.of(new Task("a"), new Task("b"), new Task("c")), original.getTasks());
        assertEquals(List.of(new Task("a"), new Task("c")), updated.getTasks());
    }

    @Test
    public void withoutTask_firstLastAndOnlyTask() {
        TaskList taskList = new TaskList(List.of(new Task("a"), new Task("b")));

        assertEquals(List.of(new Task("b")), taskList.withoutTask(Index.fromOneBased(1)).getTasks());
        assertEquals(List.of(new Task("a")), taskList.withoutTask(Index.fromOneBased(2)).getTasks());
        assertEquals(new TaskList(),
                new TaskList(List.of(new Task("a"))).withoutTask(Index.fromOneBased(1)));
    }

    @Test
    public void withoutTask_repeatedTasks_removesOnlyTheTargetedOne() {
        Task repeated = new Task("a");
        TaskList taskList = new TaskList(List.of(repeated, repeated));

        assertEquals(List.of(repeated), taskList.withoutTask(Index.fromOneBased(1)).getTasks());
    }

    @Test
    public void withoutTask_invalidArguments_throwsException() {
        TaskList taskList = new TaskList(List.of(new Task("a")));
        Index second = Index.fromOneBased(2);

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.withoutTask(second));
        assertThrows(IndexOutOfBoundsException.class, () -> new TaskList().withoutTask(Index.fromOneBased(1)));
        assertThrows(NullPointerException.class, () -> taskList.withoutTask(null));
    }

    @Test
    public void withTaskStatus_sameStatus_returnsEqualList() {
        TaskList original = new TaskList(List.of(new Task("do homework", TaskStatus.DONE)));

        assertEquals(original, original.withTaskStatus(Index.fromOneBased(1), TaskStatus.DONE));
    }

    @Test
    public void withTaskStatus_invalidArguments_throwsException() {
        TaskList taskList = new TaskList(List.of(new Task("do homework")));
        Index first = Index.fromOneBased(1);
        Index second = Index.fromOneBased(2);

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.withTaskStatus(second, TaskStatus.DONE));
        assertThrows(NullPointerException.class, () -> taskList.withTaskStatus(first, null));
    }

    @Test
    public void equals_statusMatters() {
        TaskList notDone = new TaskList(List.of(new Task("do homework")));
        TaskList done = new TaskList(List.of(new Task("do homework", TaskStatus.DONE)));

        assertFalse(notDone.equals(done));
    }

    @Test
    public void equals_orderMatters() {
        TaskList first = new TaskList(List.of(new Task("do homework"), new Task("revise notes")));
        TaskList same = new TaskList(List.of(new Task("do homework"), new Task("revise notes")));
        TaskList differentOrder = new TaskList(List.of(new Task("revise notes"), new Task("do homework")));

        assertEquals(first, same);
        assertFalse(first.equals(differentOrder));
    }

    @Test
    public void copy_doesNotEqualOriginal() {
        List<Task> source = new ArrayList<>(List.of(new Task("do homework")));
        TaskList first = new TaskList(source);
        TaskList second = first.copy();
        assertNotSame(first, second);
    }
}
