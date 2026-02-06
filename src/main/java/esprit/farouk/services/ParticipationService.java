package esprit.farouk.services;

import esprit.farouk.database.DatabaseConnection;
import esprit.farouk.models.Participation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipationService {

    private final Connection connection;

    public ParticipationService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void add(Participation participation) throws SQLException {
        String sql = "INSERT INTO participations (event_id, user_id, status, notes) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, participation.getEventId());
        ps.setLong(2, participation.getUserId());
        ps.setString(3, participation.getStatus() != null ? participation.getStatus() : "confirmed");
        ps.setString(4, participation.getNotes());
        ps.executeUpdate();
    }

    public void update(Participation participation) throws SQLException {
        String sql = "UPDATE participations SET event_id = ?, user_id = ?, status = ?, notes = ?, " +
                     "cancelled_at = ?, cancelled_reason = ?, attended = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, participation.getEventId());
        ps.setLong(2, participation.getUserId());
        ps.setString(3, participation.getStatus());
        ps.setString(4, participation.getNotes());
        if (participation.getCancelledAt() != null) ps.setTimestamp(5, Timestamp.valueOf(participation.getCancelledAt())); else ps.setNull(5, Types.TIMESTAMP);
        ps.setString(6, participation.getCancelledReason());
        ps.setBoolean(7, participation.isAttended());
        ps.setLong(8, participation.getId());
        ps.executeUpdate();
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM participations WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public List<Participation> getAll() throws SQLException {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT p.*, u.name as user_name, e.title as event_title " +
                     "FROM participations p " +
                     "LEFT JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN events e ON p.event_id = e.id " +
                     "ORDER BY p.registration_date DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Participation p = mapRow(rs);
            p.setUserName(rs.getString("user_name"));
            p.setEventTitle(rs.getString("event_title"));
            participations.add(p);
        }
        return participations;
    }

    public Participation getById(long id) throws SQLException {
        String sql = "SELECT p.*, u.name as user_name, e.title as event_title " +
                     "FROM participations p " +
                     "LEFT JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN events e ON p.event_id = e.id " +
                     "WHERE p.id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Participation p = mapRow(rs);
            p.setUserName(rs.getString("user_name"));
            p.setEventTitle(rs.getString("event_title"));
            return p;
        }
        return null;
    }

    public List<Participation> getByEventId(long eventId) throws SQLException {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT p.*, u.name as user_name, e.title as event_title " +
                     "FROM participations p " +
                     "LEFT JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN events e ON p.event_id = e.id " +
                     "WHERE p.event_id = ? ORDER BY p.registration_date ASC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, eventId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Participation p = mapRow(rs);
            p.setUserName(rs.getString("user_name"));
            p.setEventTitle(rs.getString("event_title"));
            participations.add(p);
        }
        return participations;
    }

    public List<Participation> getByUserId(long userId) throws SQLException {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT p.*, u.name as user_name, e.title as event_title " +
                     "FROM participations p " +
                     "LEFT JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN events e ON p.event_id = e.id " +
                     "WHERE p.user_id = ? ORDER BY p.registration_date DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Participation p = mapRow(rs);
            p.setUserName(rs.getString("user_name"));
            p.setEventTitle(rs.getString("event_title"));
            participations.add(p);
        }
        return participations;
    }

    public Participation getByEventAndUser(long eventId, long userId) throws SQLException {
        String sql = "SELECT p.*, u.name as user_name, e.title as event_title " +
                     "FROM participations p " +
                     "LEFT JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN events e ON p.event_id = e.id " +
                     "WHERE p.event_id = ? AND p.user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, eventId);
        ps.setLong(2, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Participation p = mapRow(rs);
            p.setUserName(rs.getString("user_name"));
            p.setEventTitle(rs.getString("event_title"));
            return p;
        }
        return null;
    }

    public void cancel(long id, String reason) throws SQLException {
        String sql = "UPDATE participations SET status = 'cancelled', cancelled_at = ?, cancelled_reason = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
        ps.setString(2, reason);
        ps.setLong(3, id);
        ps.executeUpdate();
    }

    public void markAttended(long id) throws SQLException {
        String sql = "UPDATE participations SET attended = true, status = 'attended' WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    private Participation mapRow(ResultSet rs) throws SQLException {
        Participation p = new Participation();
        p.setId(rs.getLong("id"));
        p.setEventId(rs.getLong("event_id"));
        p.setUserId(rs.getLong("user_id"));
        Timestamp regDate = rs.getTimestamp("registration_date");
        if (regDate != null) p.setRegistrationDate(regDate.toLocalDateTime());
        p.setStatus(rs.getString("status"));
        p.setNotes(rs.getString("notes"));
        Timestamp cancelledAt = rs.getTimestamp("cancelled_at");
        if (cancelledAt != null) p.setCancelledAt(cancelledAt.toLocalDateTime());
        p.setCancelledReason(rs.getString("cancelled_reason"));
        p.setAttended(rs.getBoolean("attended"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) p.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) p.setUpdatedAt(updatedAt.toLocalDateTime());
        return p;
    }
}
