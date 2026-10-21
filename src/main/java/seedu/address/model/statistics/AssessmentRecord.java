package seedu.address.model.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

/** Immutable source marks; percentages are calculated only for display. */
public record AssessmentRecord(String id, String studentId, String studentName, LocalDate date,
        String subject, String category, String title, BigDecimal score, BigDecimal maximum) {

    /** Validates marks and identifying details. */
    public AssessmentRecord {
        id = UUID.fromString(id).toString();
        studentId = UUID.fromString(studentId).toString();
        studentName = HistoryValidation.text(studentName);
        subject = HistoryValidation.text(subject);
        category = HistoryValidation.text(category);
        title = HistoryValidation.text(title);
        if (date == null || score == null || maximum == null || maximum.signum() <= 0
                || score.signum() < 0 || score.compareTo(maximum) > 0) {
            throw new IllegalArgumentException("Marks need 0 <= score <= max and max > 0, and a valid date.");
        }
        score = score.stripTrailingZeros();
        maximum = maximum.stripTrailingZeros();
    }

    /** Returns a high precision percentage for subsequent percentage-point comparisons. */
    public BigDecimal percentage() {
        return score.multiply(BigDecimal.valueOf(100)).divide(maximum, 12, RoundingMode.HALF_UP);
    }
}
