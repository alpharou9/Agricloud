package esprit.farouk.models;

import java.time.LocalDateTime;

public class Event {
    private long id;
    private long userId;
    private String title;
    private String slug;
    private String description;
    private LocalDateTime eventDate;
    private LocalDateTime endDate;
    private String location;
    private Double latitude;
    private Double longitude;
    private Integer capacity;
    private String image;
    private String category;
    private String status; // upcoming, ongoing, completed, cancelled
    private LocalDateTime registrationDeadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient field for display
    private String userName;
    private int participantCount;

    public Event() {}

    public Event(long userId, String title, String description, LocalDateTime eventDate, String location) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.slug = generateSlug(title);
        this.status = "upcoming";
    }

    public Event(long id, long userId, String title, String slug, String description,
                 LocalDateTime eventDate, LocalDateTime endDate, String location,
                 Double latitude, Double longitude, Integer capacity, String image,
                 String category, String status, LocalDateTime registrationDeadline,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.eventDate = eventDate;
        this.endDate = endDate;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.image = image;
        this.category = category;
        this.status = status;
        this.registrationDeadline = registrationDeadline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private String generateSlug(String title) {
        if (title == null) return "";
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDateTime registrationDeadline) { this.registrationDeadline = registrationDeadline; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }

    @Override
    public String toString() {
        return "Event{id=" + id + ", title='" + title + "', status='" + status + "'}";
    }
}
