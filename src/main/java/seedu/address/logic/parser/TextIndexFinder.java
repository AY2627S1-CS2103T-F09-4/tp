package seedu.address.logic.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Finds the start and end indexes of a keyword in a given text.
 */
public class TextIndexFinder {
    /**
     * Searches a text for a keyword and returns the start and end indexes of the keyword, if present.
     *
     * @param text    the text to be searched.
     * @param keyword the keyword to search for in the text.
     * @return A list of start and end indexes of all keywords found within the text.
     */
    public List<MatchRange> findMatchRanges(String text, String keyword) {
        return getMatches(text, keyword, 0);
    }

    private static List<MatchRange> getMatches(String text, String keyword, int offset) {
        List<MatchRange> matches = new ArrayList<>();

        if (text == null || keyword == null || keyword.isEmpty()) {
            return matches;
        }

        String lowerText = text.toLowerCase(Locale.ROOT);
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);

        int index = 0;

        while ((index = lowerText.indexOf(lowerKeyword, index)) != -1) {
            matches.add(new MatchRange(
                    index + offset, index + keyword.length() + offset));

            index += keyword.length();
        }

        return matches;
    }

    /**
     * Searches a text for a keyword and returns the start and end indexes of the keyword with padding, if present.
     *
     * @param text    the text to be searched.
     * @param keyword the keyword to search for in the text.
     * @param padding the offset to be applied to the indexes.
     * @return A list of start and end indexes of all keywords found within the text.
     */
    public List<MatchRange> findPaddedMatchRanges(String text, String keyword, int padding) {
        return getMatches(text, keyword, padding);
    }
}
