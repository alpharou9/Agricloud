package esprit.farouk.services;

import esprit.farouk.database.DatabaseConnection;
import esprit.farouk.models.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostService {

    private final Connection connection;

    public PostService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void add(Post post) throws SQLException {
        String sql = "INSERT INTO posts (user_id, title, slug, content, excerpt, image, category, tags, status, views) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, post.getUserId());
        ps.setString(2, post.getTitle());
        ps.setString(3, post.getSlug());
        ps.setString(4, post.getContent());
        ps.setString(5, post.getExcerpt());
        ps.setString(6, post.getImage());
        ps.setString(7, post.getCategory());
        ps.setString(8, post.getTags());
        ps.setString(9, post.getStatus() != null ? post.getStatus() : "draft");
        ps.setInt(10, post.getViews());
        ps.executeUpdate();
    }

    public void update(Post post) throws SQLException {
        String sql = "UPDATE posts SET user_id = ?, title = ?, slug = ?, content = ?, excerpt = ?, " +
                     "image = ?, category = ?, tags = ?, status = ?, views = ?, published_at = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, post.getUserId());
        ps.setString(2, post.getTitle());
        ps.setString(3, post.getSlug());
        ps.setString(4, post.getContent());
        ps.setString(5, post.getExcerpt());
        ps.setString(6, post.getImage());
        ps.setString(7, post.getCategory());
        ps.setString(8, post.getTags());
        ps.setString(9, post.getStatus());
        ps.setInt(10, post.getViews());
        if (post.getPublishedAt() != null) ps.setTimestamp(11, Timestamp.valueOf(post.getPublishedAt())); else ps.setNull(11, Types.TIMESTAMP);
        ps.setLong(12, post.getId());
        ps.executeUpdate();
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM posts WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public List<Post> getAll() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.name as user_name FROM posts p " +
                     "LEFT JOIN users u ON p.user_id = u.id ORDER BY p.created_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Post post = mapRow(rs);
            post.setUserName(rs.getString("user_name"));
            posts.add(post);
        }
        return posts;
    }

    public Post getById(long id) throws SQLException {
        String sql = "SELECT p.*, u.name as user_name FROM posts p " +
                     "LEFT JOIN users u ON p.user_id = u.id WHERE p.id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Post post = mapRow(rs);
            post.setUserName(rs.getString("user_name"));
            return post;
        }
        return null;
    }

    public List<Post> getByUserId(long userId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.name as user_name FROM posts p " +
                     "LEFT JOIN users u ON p.user_id = u.id WHERE p.user_id = ? ORDER BY p.created_at DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Post post = mapRow(rs);
            post.setUserName(rs.getString("user_name"));
            posts.add(post);
        }
        return posts;
    }

    public List<Post> getPublished() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.name as user_name FROM posts p " +
                     "LEFT JOIN users u ON p.user_id = u.id WHERE p.status = 'published' ORDER BY p.published_at DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Post post = mapRow(rs);
            post.setUserName(rs.getString("user_name"));
            posts.add(post);
        }
        return posts;
    }

    public void incrementViews(long id) throws SQLException {
        String sql = "UPDATE posts SET views = views + 1 WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public void publish(long id) throws SQLException {
        String sql = "UPDATE posts SET status = 'published', published_at = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
        ps.setLong(2, id);
        ps.executeUpdate();
    }

    public void unpublish(long id) throws SQLException {
        String sql = "UPDATE posts SET status = 'unpublished' WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    private Post mapRow(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setUserId(rs.getLong("user_id"));
        post.setTitle(rs.getString("title"));
        post.setSlug(rs.getString("slug"));
        post.setContent(rs.getString("content"));
        post.setExcerpt(rs.getString("excerpt"));
        post.setImage(rs.getString("image"));
        post.setCategory(rs.getString("category"));
        post.setTags(rs.getString("tags"));
        post.setStatus(rs.getString("status"));
        post.setViews(rs.getInt("views"));
        Timestamp publishedAt = rs.getTimestamp("published_at");
        if (publishedAt != null) post.setPublishedAt(publishedAt.toLocalDateTime());
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) post.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) post.setUpdatedAt(updatedAt.toLocalDateTime());
        return post;
    }
}
