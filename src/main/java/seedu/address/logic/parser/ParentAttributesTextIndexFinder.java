package seedu.address.logic.parser;

import java.util.List;

import seedu.address.model.person.ParentContact;

/**
 * Finds a given keyword within a {@code Person}'s attributes.
 */
public class ParentAttributesTextIndexFinder {
    private final ParentContact contact;
    private final TextIndexFinder textIndexFinder;
    private final String keyword;

    /**
     * Creates a ParentAttributesTextIndexFinder.
     *
     * @param parent a {@ParentContact}.
     * @param keyword a keyword to search within a {@ParentContact}'s attributes
     */
    public ParentAttributesTextIndexFinder(ParentContact parent, String keyword) {
        assert parent != null : "Parents must not be null";
        this.contact = parent;
        this.textIndexFinder = new TextIndexFinder();
        this.keyword = keyword;
    }

    public List<MatchRange> getNameIndex() {
        return textIndexFinder.findMatchRanges(contact.getName().fullName, keyword);
    }

    public List<MatchRange> getPhoneIndex() {
        return textIndexFinder.findMatchRanges(contact.getPhone().value, keyword);
    }

    public List<MatchRange> getEmailIndex() {
        return textIndexFinder.findMatchRanges(contact.getEmail().value, keyword);
    }

    public List<MatchRange> getLinkedStudentIndex(int padding) {
        assert padding > 0;
        return textIndexFinder.findPaddedMatchRanges(contact.getLinkedStudentName(), keyword, padding);
    }

    public List<MatchRange> getRequirementsIndex(int padding) {
        String requirements = String.join(", ", contact.getRequirements());
        return textIndexFinder.findPaddedMatchRanges(requirements, keyword, padding);
    }
}
