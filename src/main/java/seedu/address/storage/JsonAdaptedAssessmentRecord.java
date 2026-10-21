package seedu.address.storage;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.statistics.AssessmentRecord;
import seedu.address.model.statistics.HistoryValidation;

/** Jackson adapter for an assessment's raw marks and retained student identity. */
class JsonAdaptedAssessmentRecord {
    private final String id;
    private final String studentId;
    private final String studentName;
    private final String date;
    private final String subject;
    private final String category;
    private final String title;
    private final BigDecimal score;
    private final BigDecimal maximum;

    @JsonCreator
    JsonAdaptedAssessmentRecord(@JsonProperty("id") String id, @JsonProperty("studentId") String studentId,
            @JsonProperty("studentName") String studentName, @JsonProperty("date") String date,
            @JsonProperty("subject") String subject, @JsonProperty("category") String category,
            @JsonProperty("title") String title, @JsonProperty("score") BigDecimal score,
            @JsonProperty("maximum") BigDecimal maximum) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.date = date;
        this.subject = subject;
        this.category = category;
        this.title = title;
        this.score = score;
        this.maximum = maximum;
    }

    JsonAdaptedAssessmentRecord(AssessmentRecord source) {
        this(source.id(), source.studentId(), source.studentName(), source.date().toString(), source.subject(),
                source.category(), source.title(), source.score(), source.maximum());
    }

    /** Converts and validates persisted fields. */
    AssessmentRecord toModelType() throws IllegalValueException {
        try {
            return new AssessmentRecord(id, studentId, studentName, HistoryValidation.date(date),
                    subject, category, title, score, maximum);
        } catch (RuntimeException e) {
            throw new IllegalValueException("Invalid assessment history: " + e.getMessage());
        }
    }
}
