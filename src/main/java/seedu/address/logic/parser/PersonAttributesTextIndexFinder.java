package seedu.address.logic.parser;

import java.util.List;

import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Finds a given keyword within a {@code Person}'s attributes.
 */
public class PersonAttributesTextIndexFinder {
    private Person person;
    private TextIndexFinder textIndexFinder;
    private String keyword;

    /**
     * Creates a PersonAttributesTextIndexFinder.
     *
     * @param person a {@Person}.
     * @param keyword a keyword to search within a {@Person}'s attributes
     */
    public PersonAttributesTextIndexFinder (Person person, String keyword) {
        this.person = person;
        this.textIndexFinder = new TextIndexFinder();
        this.keyword = keyword;
    }

    public List<MatchRange> getNameIndex() {
        return textIndexFinder.findMatchRanges(person.getName().fullName, keyword);
    }

    public List<MatchRange> getPhoneIndex() {
        return textIndexFinder.findMatchRanges(person.getPhone().value, keyword);
    }

    public List<MatchRange> getEmailIndex() {
        return textIndexFinder.findMatchRanges(person.getEmail().value, keyword);
    }

    public List<MatchRange> getAddressIndex() {
        return textIndexFinder.findMatchRanges(person.getAddress().value, keyword);
    }

    public List<MatchRange> getGradeLevelIndex() {
        return textIndexFinder.findMatchRanges(person.getGradeLevel().value, keyword);
    }

    public List<MatchRange> getSubjectIndex() {
        return textIndexFinder.findMatchRanges(person.getSubject().value, keyword);
    }

    public List<MatchRange> getTaskIndex(int i) {
        return textIndexFinder.findMatchRanges(person.getTaskList().getTasks().get(i).description, keyword);
    }

    public List<MatchRange> getTagIndex(Tag tag) {
        return textIndexFinder.findMatchRanges(tag.tagName, keyword);
    }
}
