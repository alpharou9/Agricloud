package esprit.farouk.models;

import java.time.LocalDateTime;

public class Participation {
    private long id;
    private long eventId;
    private long userId;
    private LocalDateTime registrationDate;
    private String status; // pending, confirmed, cancelled, attended
    private String notes;
    private LocalDateTime cancelledAt;
    private String cancelledReason;
    private boolean attended;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient fields for display
    private String userName;
    private String eventTitle;

    public Participation() {}

    public Participation(long eventId, long userId) {
        this.eventId = eventId;
        this.userId = userId;
        this.status = "confirmed";
        this.attended = false;
    }

    public Participation(long id, long eventId, long userId, LocalDateTime registrationDate,
                         String status, String notes, LocalDateTime cancelledAt,
                         String cancelledReason, boolean attended,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.registrationDate = registrationDate;
        this.status = status;
        this.notes = notes;
        this.cancelledAt = cancelledAt;
        this.cancelledReason = cancelledReason;
        this.attended = attended;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getEventId() { return eventId; }
    public void setEventId(long eventId) { this.eventId = eventId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancelledReason() { return cancelledReason; }
    public void setCancelledReason(String cancelledReason) { this.cancelledReason = cancelledReason; }

    public boolean isAttended() { return attended; }
    public void setAttended(boolean attended) { this.attended = attended; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    @Override
    public String toString() {
        return "Participation{id=" + id + ", eventId=" + eventId + ", userId=" + userId + ", status='" + status + "'}";
    }
}
