package seedu.address.logic.parser;

/**
 * Represents the start and end indexes of a String.
 */
public class MatchRange {
    private final int start;
    private final int end;

    /**
     * Creates a MatchRange.
     *
     * @param start the starting index.
     * @param end the ending index.
     */
    public MatchRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the starting index of a String.
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the ending index of a String.
     */
    public int getEnd() {
        return end;
    }
}
