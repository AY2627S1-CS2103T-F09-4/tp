package seedu.address.model.schedule;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Represents a time range within a single day.
 * Guarantees: immutable; start is strictly before end.
 */
public class TimeSlot {

    public static final String MESSAGE_CONSTRAINTS =
            "Times should be in HH-mm format (e.g. 10-00), and 'from' must be earlier than 'to'.";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH-mm");

    public final LocalTime start;
    public final LocalTime end;

    /**
     * Constructs a {@code TimeSlot}.
     *
     * @param start Start of the slot.
     * @param end End of the slot, must be after {@code start}.
     */
    public TimeSlot(LocalTime start, LocalTime end) {
        requireNonNull(start);
        requireNonNull(end);
        checkArgument(start.isBefore(end), MESSAGE_CONSTRAINTS);
        this.start = start;
        this.end = end;
    }

    /**
     * Parses a {@code TimeSlot} from two strings in HH-mm format.
     */
    public static TimeSlot of(String start, String end) {
        requireNonNull(start);
        requireNonNull(end);
        checkArgument(isValidTimeSlot(start, end), MESSAGE_CONSTRAINTS);
        return new TimeSlot(LocalTime.parse(start, FORMATTER), LocalTime.parse(end, FORMATTER));
    }

    /**
     * Returns true if both strings are valid HH-mm times and {@code start} is before {@code end}.
     */
    public static boolean isValidTimeSlot(String start, String end) {
        try {
            return LocalTime.parse(start, FORMATTER).isBefore(LocalTime.parse(end, FORMATTER));
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if this slot shares any time with {@code other}.
     * Slots that only touch at a boundary do not overlap.
     */
    public boolean overlaps(TimeSlot other) {
        requireNonNull(other);
        return start.isBefore(other.end) && other.start.isBefore(end);
    }

    /** Returns the storage-friendly string for the start time. */
    public String getStartString() {
        return start.format(FORMATTER);
    }

    /** Returns the storage-friendly string for the end time. */
    public String getEndString() {
        return end.format(FORMATTER);
    }

    @Override
    public String toString() {
        return getStartString() + " to " + getEndString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TimeSlot otherSlot)) {
            return false;
        }
        return start.equals(otherSlot.start) && end.equals(otherSlot.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
