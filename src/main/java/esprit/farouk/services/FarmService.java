package esprit.farouk.services;

import esprit.farouk.database.DatabaseConnection;
import esprit.farouk.models.Farm;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FarmService {

    private final Connection connection;

    public FarmService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void add(Farm farm) throws SQLException {
        String sql = "INSERT INTO farms (user_id, name, location, latitude, longitude, area, farm_type, description, image, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, farm.getUserId());
        ps.setString(2, farm.getName());
        ps.setString(3, farm.getLocation());
        if (farm.getLatitude() != null) ps.setDouble(4, farm.getLatitude()); else ps.setNull(4, Types.DECIMAL);
        if (farm.getLongitude() != null) ps.setDouble(5, farm.getLongitude()); else ps.setNull(5, Types.DECIMAL);
        if (farm.getArea() != null) ps.setDouble(6, farm.getArea()); else ps.setNull(6, Types.DECIMAL);
        ps.setString(7, farm.getFarmType());
        ps.setString(8, farm.getDescription());
        ps.setString(9, farm.getImage());
        ps.setString(10, farm.getStatus() != null ? farm.getStatus() : "pending");
        ps.executeUpdate();
    }

    public void update(Farm farm) throws SQLException {
        String sql = "UPDATE farms SET user_id = ?, name = ?, location = ?, latitude = ?, longitude = ?, " +
                     "area = ?, farm_type = ?, description = ?, image = ?, status = ?, " +
                     "approved_at = ?, approved_by = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, farm.getUserId());
        ps.setString(2, farm.getName());
        ps.setString(3, farm.getLocation());
        if (farm.getLatitude() != null) ps.setDouble(4, farm.getLatitude()); else ps.setNull(4, Types.DECIMAL);
        if (farm.getLongitude() != null) ps.setDouble(5, farm.getLongitude()); else ps.setNull(5, Types.DECIMAL);
        if (farm.getArea() != null) ps.setDouble(6, farm.getArea()); else ps.setNull(6, Types.DECIMAL);
        ps.setString(7, farm.getFarmType());
        ps.setString(8, farm.getDescription());
        ps.setString(9, farm.getImage());
        ps.setString(10, farm.getStatus());
        if (farm.getApprovedAt() != null) ps.setTimestamp(11, Timestamp.valueOf(farm.getApprovedAt())); else ps.setNull(11, Types.TIMESTAMP);
        if (farm.getApprovedBy() != null) ps.setLong(12, farm.getApprovedBy()); else ps.setNull(12, Types.BIGINT);
        ps.setLong(13, farm.getId());
        ps.executeUpdate();
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM farms WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public List<Farm> getAll() throws SQLException {
        List<Farm> farms = new ArrayList<>();
        String sql = "SELECT * FROM farms";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            farms.add(mapRow(rs));
        }
        return farms;
    }

    public Farm getById(long id) throws SQLException {
        String sql = "SELECT * FROM farms WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    public List<Farm> getByUserId(long userId) throws SQLException {
        List<Farm> farms = new ArrayList<>();
        String sql = "SELECT * FROM farms WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            farms.add(mapRow(rs));
        }
        return farms;
    }

    private Farm mapRow(ResultSet rs) throws SQLException {
        Farm farm = new Farm();
        farm.setId(rs.getLong("id"));
        farm.setUserId(rs.getLong("user_id"));
        farm.setName(rs.getString("name"));
        farm.setLocation(rs.getString("location"));
        double lat = rs.getDouble("latitude");
        if (!rs.wasNull()) farm.setLatitude(lat);
        double lng = rs.getDouble("longitude");
        if (!rs.wasNull()) farm.setLongitude(lng);
        double area = rs.getDouble("area");
        if (!rs.wasNull()) farm.setArea(area);
        farm.setFarmType(rs.getString("farm_type"));
        farm.setDescription(rs.getString("description"));
        farm.setImage(rs.getString("image"));
        farm.setStatus(rs.getString("status"));
        Timestamp approvedAt = rs.getTimestamp("approved_at");
        if (approvedAt != null) farm.setApprovedAt(approvedAt.toLocalDateTime());
        long approvedBy = rs.getLong("approved_by");
        if (!rs.wasNull()) farm.setApprovedBy(approvedBy);
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) farm.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) farm.setUpdatedAt(updatedAt.toLocalDateTime());
        return farm;
    }
}
