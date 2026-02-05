package esprit.farouk.models;

import java.time.LocalDateTime;

public class Field {
    private long id;
    private long farmId;
    private String name;
    private double area;
    private String soilType;
    private String cropType;
    private String coordinates; // JSON string
    private String status; // active, inactive, fallow
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Field() {}

    public Field(long farmId, String name, double area) {
        this.farmId = farmId;
        this.name = name;
        this.area = area;
        this.status = "active";
    }

    public Field(long id, long farmId, String name, double area, String soilType, String cropType,
                 String coordinates, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.farmId = farmId;
        this.name = name;
        this.area = area;
        this.soilType = soilType;
        this.cropType = cropType;
        this.coordinates = coordinates;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getFarmId() { return farmId; }
    public void setFarmId(long farmId) { this.farmId = farmId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }

    public String getCoordinates() { return coordinates; }
    public void setCoordinates(String coordinates) { this.coordinates = coordinates; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Field{id=" + id + ", name='" + name + "', area=" + area + ", status='" + status + "'}";
    }
}
