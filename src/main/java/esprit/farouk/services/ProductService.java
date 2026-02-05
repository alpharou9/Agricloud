package esprit.farouk.services;

import esprit.farouk.database.DatabaseConnection;
import esprit.farouk.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {

    private final Connection connection;

    public ProductService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void add(Product product) throws SQLException {
        String sql = "INSERT INTO products (user_id, farm_id, name, description, price, quantity, unit, category, image, status, views) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, product.getUserId());
        if (product.getFarmId() != null) ps.setLong(2, product.getFarmId()); else ps.setNull(2, Types.BIGINT);
        ps.setString(3, product.getName());
        ps.setString(4, product.getDescription());
        ps.setDouble(5, product.getPrice());
        ps.setInt(6, product.getQuantity());
        ps.setString(7, product.getUnit());
        ps.setString(8, product.getCategory());
        ps.setString(9, product.getImage());
        ps.setString(10, product.getStatus() != null ? product.getStatus() : "pending");
        ps.setInt(11, product.getViews());
        ps.executeUpdate();
    }

    public void update(Product product) throws SQLException {
        String sql = "UPDATE products SET user_id = ?, farm_id = ?, name = ?, description = ?, price = ?, " +
                     "quantity = ?, unit = ?, category = ?, image = ?, status = ?, views = ?, " +
                     "approved_at = ?, approved_by = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, product.getUserId());
        if (product.getFarmId() != null) ps.setLong(2, product.getFarmId()); else ps.setNull(2, Types.BIGINT);
        ps.setString(3, product.getName());
        ps.setString(4, product.getDescription());
        ps.setDouble(5, product.getPrice());
        ps.setInt(6, product.getQuantity());
        ps.setString(7, product.getUnit());
        ps.setString(8, product.getCategory());
        ps.setString(9, product.getImage());
        ps.setString(10, product.getStatus());
        ps.setInt(11, product.getViews());
        if (product.getApprovedAt() != null) ps.setTimestamp(12, Timestamp.valueOf(product.getApprovedAt())); else ps.setNull(12, Types.TIMESTAMP);
        if (product.getApprovedBy() != null) ps.setLong(13, product.getApprovedBy()); else ps.setNull(13, Types.BIGINT);
        ps.setLong(14, product.getId());
        ps.executeUpdate();
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public List<Product> getAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            products.add(mapRow(rs));
        }
        return products;
    }

    public Product getById(long id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    public List<Product> getByUserId(long userId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            products.add(mapRow(rs));
        }
        return products;
    }

    public List<Product> getApproved() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE status = 'approved' AND quantity > 0";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            products.add(mapRow(rs));
        }
        return products;
    }

    public void incrementViews(long id) throws SQLException {
        String sql = "UPDATE products SET views = views + 1 WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public void decrementQuantity(long id, int amount) throws SQLException {
        String sql = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, amount);
        ps.setLong(2, id);
        ps.setInt(3, amount);
        int rows = ps.executeUpdate();
        if (rows == 0) {
            throw new SQLException("Insufficient stock or product not found");
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setUserId(rs.getLong("user_id"));
        long farmId = rs.getLong("farm_id");
        if (!rs.wasNull()) product.setFarmId(farmId);
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getDouble("price"));
        product.setQuantity(rs.getInt("quantity"));
        product.setUnit(rs.getString("unit"));
        product.setCategory(rs.getString("category"));
        product.setImage(rs.getString("image"));
        product.setStatus(rs.getString("status"));
        product.setViews(rs.getInt("views"));
        Timestamp approvedAt = rs.getTimestamp("approved_at");
        if (approvedAt != null) product.setApprovedAt(approvedAt.toLocalDateTime());
        long approvedBy = rs.getLong("approved_by");
        if (!rs.wasNull()) product.setApprovedBy(approvedBy);
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) product.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) product.setUpdatedAt(updatedAt.toLocalDateTime());
        return product;
    }
}
