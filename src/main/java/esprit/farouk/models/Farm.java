package esprit.farouk.models;

import java.time.LocalDateTime;

public class Farm {
    private long id;
    private long userId;
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private Double area;
    private String farmType;
    private String description;
    private String image;
    private String status; // pending, approved, rejected, inactive
    private LocalDateTime approvedAt;
    private Long approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Farm() {}

    public Farm(long userId, String name, String location) {
        this.userId = userId;
        this.name = name;
        this.location = location;
        this.status = "pending";
    }

    public Farm(long id, long userId, String name, String location, Double latitude, Double longitude,
                Double area, String farmType, String description, String image, String status,
                LocalDateTime approvedAt, Long approvedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = area;
        this.farmType = farmType;
        this.description = description;
        this.image = image;
        this.status = status;
        this.approvedAt = approvedAt;
        this.approvedBy = approvedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public String getFarmType() { return farmType; }
    public void setFarmType(String farmType) { this.farmType = farmType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Farm{id=" + id + ", name='" + name + "', location='" + location + "', status='" + status + "'}";
    }
}
