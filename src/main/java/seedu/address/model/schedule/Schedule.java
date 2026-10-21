package seedu.address.model.schedule;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of {@code ScheduledSession}s. Overlapping sessions are allowed.
 */
public class Schedule {

    private final ObservableList<ScheduledSession> sessions = FXCollections.observableArrayList();
    private final ObservableList<ScheduledSession> unmodifiableSessions =
            FXCollections.unmodifiableObservableList(sessions);

    /** Adds {@code session} to the schedule. */
    public void add(ScheduledSession session) {
        requireNonNull(session);
        if (sessions.stream().anyMatch(s -> s.getSessionId().equals(session.getSessionId()))) {
            throw new IllegalArgumentException("Scheduled session IDs must be unique.");
        }
        sessions.add(session);
    }

    /** Returns true if an equal session is in the schedule. */
    public boolean contains(ScheduledSession session) {
        requireNonNull(session);
        return sessions.contains(session);
    }

    /** Removes the first session equal to {@code session}. Returns true if one was found. */
    public boolean remove(ScheduledSession session) {
        requireNonNull(session);
        return sessions.remove(session);
    }

    /** Removes all sessions matching {@code predicate}. */
    public void removeIf(Predicate<ScheduledSession> predicate) {
        requireNonNull(predicate);
        sessions.removeIf(predicate);
    }

    /** Replaces the contents of this schedule with {@code replacement}. */
    public void setSessions(List<ScheduledSession> replacement) {
        requireNonNull(replacement);
        if (replacement.stream().map(ScheduledSession::getSessionId).distinct().count() != replacement.size()) {
            throw new IllegalArgumentException("Scheduled session IDs must be unique.");
        }
        sessions.setAll(replacement);
    }

    /** Returns an unmodifiable view of the sessions. */
    public ObservableList<ScheduledSession> asUnmodifiableObservableList() {
        return unmodifiableSessions;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof Schedule otherSchedule && sessions.equals(otherSchedule.sessions);
    }

    @Override
    public int hashCode() {
        return sessions.hashCode();
    }

    @Override
    public String toString() {
        return sessions.toString();
    }
}
