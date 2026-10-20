package seedu.address.model.reminder;

/** Display urgency of a reminder at a particular local time. */
public enum ReminderUrgency {
    OVERDUE("OVERDUE"),
    DUE_SOON("DUE SOON"),
    UPCOMING("UPCOMING"),
    EVERY_STARTUP("EVERY STARTUP");

    private final String displayName;

    ReminderUrgency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
