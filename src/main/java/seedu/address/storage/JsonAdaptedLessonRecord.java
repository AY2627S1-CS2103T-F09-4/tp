package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.statistics.HistoryValidation;
import seedu.address.model.statistics.LessonRecord;
import seedu.address.model.statistics.LessonRecord.Outcome;

/** Jackson adapter storing historical source details rather than calculated totals. */
class JsonAdaptedLessonRecord {
    private final String id;
    private final String studentId;
    private final String studentName;
    private final String subject;
    private final String date;
    private final String start;
    private final String end;
    private final String outcome;
    private final String sourceSessionId;

    @JsonCreator
    JsonAdaptedLessonRecord(@JsonProperty("id") String id, @JsonProperty("studentId") String studentId,
            @JsonProperty("studentName") String studentName, @JsonProperty("subject") String subject,
            @JsonProperty("date") String date, @JsonProperty("start") String start,
            @JsonProperty("end") String end, @JsonProperty("outcome") String outcome,
            @JsonProperty("sourceSessionId") String sourceSessionId) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.subject = subject;
        this.date = date;
        this.start = start;
        this.end = end;
        this.outcome = outcome;
        this.sourceSessionId = sourceSessionId;
    }

    JsonAdaptedLessonRecord(LessonRecord source) {
        this(source.id(), source.studentId(), source.studentName(), source.subject(), source.date().toString(),
                source.start().toString(), source.end().toString(), source.outcome().name(), source.sourceSessionId());
    }

    /** Converts and validates all stored fields; retained deleted-student identities are allowed. */
    LessonRecord toModelType() throws IllegalValueException {
        try {
            return new LessonRecord(id, studentId, studentName, subject, HistoryValidation.date(date),
                    HistoryValidation.time(start), HistoryValidation.time(end),
                    Outcome.parse(outcome), sourceSessionId);
        } catch (RuntimeException e) {
            throw new IllegalValueException("Invalid lesson history: " + e.getMessage());
        }
    }
}
