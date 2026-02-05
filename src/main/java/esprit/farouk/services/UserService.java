package esprit.farouk.services;

import esprit.farouk.database.DatabaseConnection;
import esprit.farouk.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final Connection connection;

    public UserService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void add(User user) throws SQLException {
        String sql = "INSERT INTO users (role_id, name, email, password, phone, profile_picture, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, user.getRoleId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getEmail());
        ps.setString(4, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        ps.setString(5, user.getPhone());
        ps.setString(6, user.getProfilePicture());
        ps.setString(7, user.getStatus() != null ? user.getStatus() : "active");
        ps.executeUpdate();
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET role_id = ?, name = ?, email = ?, phone = ?, " +
                     "profile_picture = ?, status = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, user.getRoleId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getPhone());
        ps.setString(5, user.getProfilePicture());
        ps.setString(6, user.getStatus());
        ps.setLong(7, user.getId());
        ps.executeUpdate();
    }

    public void updatePassword(long userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        ps.setLong(2, userId);
        ps.executeUpdate();
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            users.add(mapRow(rs));
        }
        return users;
    }

    public User getById(long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    public User getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    public User authenticate(String email, String password) throws SQLException {
        User user = getByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setRoleId(rs.getLong("role_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setProfilePicture(rs.getString("profile_picture"));
        user.setStatus(rs.getString("status"));
        Timestamp emailVerified = rs.getTimestamp("email_verified_at");
        if (emailVerified != null) user.setEmailVerifiedAt(emailVerified.toLocalDateTime());
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) user.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) user.setUpdatedAt(updatedAt.toLocalDateTime());
        return user;
    }
}
