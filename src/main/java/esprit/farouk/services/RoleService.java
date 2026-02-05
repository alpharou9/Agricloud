package esprit.farouk.services;

import esprit.farouk.database.DatabaseConnection;
import esprit.farouk.models.Role;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoleService {

    private final Connection connection;

    public RoleService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void add(Role role) throws SQLException {
        String sql = "INSERT INTO roles (name, description, permissions) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, role.getName());
        ps.setString(2, role.getDescription());
        ps.setString(3, role.getPermissions());
        ps.executeUpdate();
    }

    public void update(Role role) throws SQLException {
        String sql = "UPDATE roles SET name = ?, description = ?, permissions = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, role.getName());
        ps.setString(2, role.getDescription());
        ps.setString(3, role.getPermissions());
        ps.setLong(4, role.getId());
        ps.executeUpdate();
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM roles WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public List<Role> getAll() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            roles.add(mapRow(rs));
        }
        return roles;
    }

    public Role getById(long id) throws SQLException {
        String sql = "SELECT * FROM roles WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    public Role getByName(String name) throws SQLException {
        String sql = "SELECT * FROM roles WHERE name = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    private Role mapRow(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));
        role.setDescription(rs.getString("description"));
        role.setPermissions(rs.getString("permissions"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) role.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) role.setUpdatedAt(updatedAt.toLocalDateTime());
        return role;
    }
}
