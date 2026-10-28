package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import seedu.address.model.AddressBook;

/** handles snapshotting multiple address books after commands for undoing and redoing */
public class SnapshotManager {
    private static final List<AddressBook> snapshots =
            new ArrayList<>();
    private static final List<String> snapshotCommands = new ArrayList<>();

    private static int currentIndex = -1;
    private static String currentCommand;

    /**
     * Saves the current address book as a snapshot in {@code @snapshots} , and clears the rest of the snapshots
     * since they can't be redone.
     *
     * @param addressBook the address book to be saved.
     */
    public void saveSnapshot(AddressBook addressBook, String feedbackToUser) {
        // Discards snapshots that belong to the old history branch after an undo.
        if (currentIndex == -1) {
            snapshots.add(addressBook.copy());
            snapshotCommands.add(feedbackToUser);
            currentIndex = snapshots.size() - 1;
        } else if (!snapshots.get(currentIndex).equals(addressBook.copy())) {
            while (snapshots.size() > currentIndex + 1) {
                snapshots.remove(snapshots.size() - 1);
                snapshotCommands.remove(snapshotCommands.size() - 1);
            }
            snapshots.add(addressBook.copy());
            snapshotCommands.add(feedbackToUser);
            currentIndex = snapshots.size() - 1;
        }
    }

    /**
     * Undoes the last command by returning the previous address book snapshot.
     *
     * @return the previous address book snapshot.
     */
    public AddressBook undo() {
        if (currentIndex <= 0) {
            return null;
        }

        currentCommand = snapshotCommands.get(currentIndex);
        currentIndex--;
        return snapshots.get(currentIndex).copy();
    }

    public String getCommandMessage() {
        if (currentIndex < 0) {
            return null;
        }
        return currentCommand;
    }

    /**
     * Redoes the last command that was undone by returning the next address book snapshot.
     *
     * @return the next address book snapshot.
     */
    public AddressBook redo() {
        if (currentIndex >= snapshots.size() - 1) {
            return null;
        }

        currentIndex++;
        currentCommand = snapshotCommands.get(currentIndex);
        return snapshots.get(currentIndex).copy();
    }

    /** Indicates whether a previous snapshot can be reached. */
    public static boolean canUndo() {
        return currentIndex > 0;
    }

    /** Indicates whether the next snapshot can be reached. */
    public static boolean canRedo() {
        return currentIndex < snapshots.size() - 1;
    }

    /** resets the snapshots and index for testing. */
    public static void reset() {
        currentIndex = -1;
        while (!snapshots.isEmpty()) {
            snapshots.removeLast();
        }
        while (!snapshotCommands.isEmpty()) {
            snapshotCommands.removeLast();
        }
    }
}
