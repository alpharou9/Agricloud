package esprit.farouk.services;

import esprit.farouk.database.DatabaseConnection;
import esprit.farouk.models.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentService {

    private final Connection connection;

    public CommentService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void add(Comment comment) throws SQLException {
        String sql = "INSERT INTO comments (post_id, user_id, parent_comment_id, content, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, comment.getPostId());
        ps.setLong(2, comment.getUserId());
        if (comment.getParentCommentId() != null) ps.setLong(3, comment.getParentCommentId()); else ps.setNull(3, Types.BIGINT);
        ps.setString(4, comment.getContent());
        ps.setString(5, comment.getStatus() != null ? comment.getStatus() : "approved");
        ps.executeUpdate();
    }

    public void update(Comment comment) throws SQLException {
        String sql = "UPDATE comments SET post_id = ?, user_id = ?, parent_comment_id = ?, content = ?, " +
                     "status = ?, approved_at = ?, approved_by = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, comment.getPostId());
        ps.setLong(2, comment.getUserId());
        if (comment.getParentCommentId() != null) ps.setLong(3, comment.getParentCommentId()); else ps.setNull(3, Types.BIGINT);
        ps.setString(4, comment.getContent());
        ps.setString(5, comment.getStatus());
        if (comment.getApprovedAt() != null) ps.setTimestamp(6, Timestamp.valueOf(comment.getApprovedAt())); else ps.setNull(6, Types.TIMESTAMP);
        if (comment.getApprovedBy() != null) ps.setLong(7, comment.getApprovedBy()); else ps.setNull(7, Types.BIGINT);
        ps.setLong(8, comment.getId());
        ps.executeUpdate();
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM comments WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public List<Comment> getAll() throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.name as user_name FROM comments c " +
                     "LEFT JOIN users u ON c.user_id = u.id ORDER BY c.created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Comment comment = mapRow(rs);
            comment.setUserName(rs.getString("user_name"));
            comments.add(comment);
        }
        return comments;
    }

    public Comment getById(long id) throws SQLException {
        String sql = "SELECT c.*, u.name as user_name FROM comments c " +
                     "LEFT JOIN users u ON c.user_id = u.id WHERE c.id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Comment comment = mapRow(rs);
            comment.setUserName(rs.getString("user_name"));
            return comment;
        }
        return null;
    }

    public List<Comment> getByPostId(long postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.name as user_name FROM comments c " +
                     "LEFT JOIN users u ON c.user_id = u.id " +
                     "WHERE c.post_id = ? ORDER BY c.created_at ASC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, postId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Comment comment = mapRow(rs);
            comment.setUserName(rs.getString("user_name"));
            comments.add(comment);
        }
        return comments;
    }

    public List<Comment> getApprovedByPostId(long postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.name as user_name FROM comments c " +
                     "LEFT JOIN users u ON c.user_id = u.id " +
                     "WHERE c.post_id = ? AND c.status = 'approved' ORDER BY c.created_at ASC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, postId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Comment comment = mapRow(rs);
            comment.setUserName(rs.getString("user_name"));
            comments.add(comment);
        }
        return comments;
    }

    public void approve(long id, long approvedBy) throws SQLException {
        String sql = "UPDATE comments SET status = 'approved', approved_at = ?, approved_by = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
        ps.setLong(2, approvedBy);
        ps.setLong(3, id);
        ps.executeUpdate();
    }

    public void reject(long id) throws SQLException {
        String sql = "UPDATE comments SET status = 'rejected' WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    private Comment mapRow(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setUserId(rs.getLong("user_id"));
        long parentId = rs.getLong("parent_comment_id");
        if (!rs.wasNull()) comment.setParentCommentId(parentId);
        comment.setContent(rs.getString("content"));
        comment.setStatus(rs.getString("status"));
        Timestamp approvedAt = rs.getTimestamp("approved_at");
        if (approvedAt != null) comment.setApprovedAt(approvedAt.toLocalDateTime());
        long approvedBy = rs.getLong("approved_by");
        if (!rs.wasNull()) comment.setApprovedBy(approvedBy);
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) comment.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) comment.setUpdatedAt(updatedAt.toLocalDateTime());
        return comment;
    }
}
