package seedu.address.logic.parser;

import java.util.List;

import seedu.address.model.person.ParentContact;

/**
 * Finds a given keyword within a {@code Person}'s attributes.
 */
public class ParentAttributesTextIndexFinder {
    private ParentContact contact;
    private TextIndexFinder textIndexFinder;
    private String keyword;

    /**
     * Creates a ParentAttributesTextIndexFinder.
     *
     * @param parent a {@ParentContact}.
     * @param keyword a keyword to search within a {@ParentContact}'s attributes
     */
    public ParentAttributesTextIndexFinder(ParentContact parent, String keyword) {
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
        return textIndexFinder.findPaddedMatchRanges(contact.getLinkedStudentId(), keyword, padding);
    }

    public List<MatchRange> getRequirementsIndex(int padding) {
        String requirements = String.join(", ", contact.getRequirements());
        return textIndexFinder.findPaddedMatchRanges(requirements, keyword, padding);
    }
}
