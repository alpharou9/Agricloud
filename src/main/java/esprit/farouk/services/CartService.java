package esprit.farouk.services;

import esprit.farouk.database.DatabaseConnection;
import esprit.farouk.models.CartItem;
import esprit.farouk.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartService {

    private final Connection connection;
    private final ProductService productService;

    public CartService() {
        this.connection = DatabaseConnection.getConnection();
        this.productService = new ProductService();
    }

    public void addToCart(long userId, long productId, int quantity) throws SQLException {
        // Check if item already in cart
        CartItem existing = getCartItem(userId, productId);
        if (existing != null) {
            // Update quantity
            String sql = "UPDATE shopping_cart SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setLong(2, userId);
            ps.setLong(3, productId);
            ps.executeUpdate();
        } else {
            // Insert new item
            String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        }
    }

    public void updateQuantity(long userId, long productId, int quantity) throws SQLException {
        if (quantity <= 0) {
            removeFromCart(userId, productId);
            return;
        }
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, quantity);
        ps.setLong(2, userId);
        ps.setLong(3, productId);
        ps.executeUpdate();
    }

    public void removeFromCart(long userId, long productId) throws SQLException {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ps.setLong(2, productId);
        ps.executeUpdate();
    }

    public void clearCart(long userId) throws SQLException {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ps.executeUpdate();
    }

    public List<CartItem> getCartItems(long userId) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT sc.*, p.name as product_name, p.price as product_price, p.unit as product_unit " +
                     "FROM shopping_cart sc " +
                     "JOIN products p ON sc.product_id = p.id " +
                     "WHERE sc.user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            CartItem item = mapRow(rs);
            item.setProductName(rs.getString("product_name"));
            item.setProductPrice(rs.getDouble("product_price"));
            item.setProductUnit(rs.getString("product_unit"));
            items.add(item);
        }
        return items;
    }

    public CartItem getCartItem(long userId, long productId) throws SQLException {
        String sql = "SELECT * FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ps.setLong(2, productId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    public int getCartCount(long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM shopping_cart WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public double getCartTotal(long userId) throws SQLException {
        String sql = "SELECT SUM(sc.quantity * p.price) FROM shopping_cart sc " +
                     "JOIN products p ON sc.product_id = p.id " +
                     "WHERE sc.user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getDouble(1);
        }
        return 0;
    }

    private CartItem mapRow(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setId(rs.getLong("id"));
        item.setUserId(rs.getLong("user_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) item.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) item.setUpdatedAt(updatedAt.toLocalDateTime());
        return item;
    }
}
