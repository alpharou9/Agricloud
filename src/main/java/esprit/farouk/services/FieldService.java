package esprit.farouk.services;

import esprit.farouk.database.DatabaseConnection;
import esprit.farouk.models.Field;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FieldService {

    private final Connection connection;

    public FieldService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void add(Field field) throws SQLException {
        String sql = "INSERT INTO fields (farm_id, name, area, soil_type, crop_type, coordinates, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, field.getFarmId());
        ps.setString(2, field.getName());
        ps.setDouble(3, field.getArea());
        ps.setString(4, field.getSoilType());
        ps.setString(5, field.getCropType());
        ps.setString(6, field.getCoordinates());
        ps.setString(7, field.getStatus() != null ? field.getStatus() : "active");
        ps.executeUpdate();
    }

    public void update(Field field) throws SQLException {
        String sql = "UPDATE fields SET farm_id = ?, name = ?, area = ?, soil_type = ?, " +
                     "crop_type = ?, coordinates = ?, status = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, field.getFarmId());
        ps.setString(2, field.getName());
        ps.setDouble(3, field.getArea());
        ps.setString(4, field.getSoilType());
        ps.setString(5, field.getCropType());
        ps.setString(6, field.getCoordinates());
        ps.setString(7, field.getStatus());
        ps.setLong(8, field.getId());
        ps.executeUpdate();
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM fields WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public List<Field> getAll() throws SQLException {
        List<Field> fields = new ArrayList<>();
        String sql = "SELECT * FROM fields";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            fields.add(mapRow(rs));
        }
        return fields;
    }

    public Field getById(long id) throws SQLException {
        String sql = "SELECT * FROM fields WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    public List<Field> getByFarmId(long farmId) throws SQLException {
        List<Field> fields = new ArrayList<>();
        String sql = "SELECT * FROM fields WHERE farm_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, farmId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            fields.add(mapRow(rs));
        }
        return fields;
    }

    private Field mapRow(ResultSet rs) throws SQLException {
        Field field = new Field();
        field.setId(rs.getLong("id"));
        field.setFarmId(rs.getLong("farm_id"));
        field.setName(rs.getString("name"));
        field.setArea(rs.getDouble("area"));
        field.setSoilType(rs.getString("soil_type"));
        field.setCropType(rs.getString("crop_type"));
        field.setCoordinates(rs.getString("coordinates"));
        field.setStatus(rs.getString("status"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) field.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) field.setUpdatedAt(updatedAt.toLocalDateTime());
        return field;
    }
}
