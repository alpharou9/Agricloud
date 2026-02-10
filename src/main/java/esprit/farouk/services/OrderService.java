package esprit.farouk.services;

import esprit.farouk.database.DatabaseConnection;
import esprit.farouk.models.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final Connection connection;

    public OrderService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void add(Order order) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, product_id, seller_id, quantity, unit_price, total_price, " +
                     "status, shipping_address, shipping_city, shipping_postal, shipping_email, shipping_phone, notes, order_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, order.getCustomerId());
        ps.setLong(2, order.getProductId());
        ps.setLong(3, order.getSellerId());
        ps.setInt(4, order.getQuantity());
        ps.setDouble(5, order.getUnitPrice());
        ps.setDouble(6, order.getTotalPrice());
        ps.setString(7, order.getStatus() != null ? order.getStatus() : "pending");
        ps.setString(8, order.getShippingAddress());
        ps.setString(9, order.getShippingCity());
        ps.setString(10, order.getShippingPostal());
        ps.setString(11, order.getShippingEmail());
        ps.setString(12, order.getShippingPhone());
        ps.setString(13, order.getNotes());
        ps.setTimestamp(14, order.getOrderDate() != null ? Timestamp.valueOf(order.getOrderDate()) : Timestamp.valueOf(java.time.LocalDateTime.now()));
        ps.executeUpdate();
    }

    public void update(Order order) throws SQLException {
        String sql = "UPDATE orders SET customer_id = ?, product_id = ?, seller_id = ?, quantity = ?, " +
                     "unit_price = ?, total_price = ?, status = ?, shipping_address = ?, shipping_city = ?, " +
                     "shipping_postal = ?, shipping_email = ?, shipping_phone = ?, notes = ?, delivery_date = ?, cancelled_at = ?, cancelled_reason = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, order.getCustomerId());
        ps.setLong(2, order.getProductId());
        ps.setLong(3, order.getSellerId());
        ps.setInt(4, order.getQuantity());
        ps.setDouble(5, order.getUnitPrice());
        ps.setDouble(6, order.getTotalPrice());
        ps.setString(7, order.getStatus());
        ps.setString(8, order.getShippingAddress());
        ps.setString(9, order.getShippingCity());
        ps.setString(10, order.getShippingPostal());
        ps.setString(11, order.getShippingEmail());
        ps.setString(12, order.getShippingPhone());
        ps.setString(13, order.getNotes());
        if (order.getDeliveryDate() != null) ps.setDate(14, Date.valueOf(order.getDeliveryDate())); else ps.setNull(14, Types.DATE);
        if (order.getCancelledAt() != null) ps.setTimestamp(15, Timestamp.valueOf(order.getCancelledAt())); else ps.setNull(15, Types.TIMESTAMP);
        ps.setString(16, order.getCancelledReason());
        ps.setLong(17, order.getId());
        ps.executeUpdate();
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
    }

    public List<Order> getAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            orders.add(mapRow(rs));
        }
        return orders;
    }

    public Order getById(long id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    public List<Order> getByCustomerId(long customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, customerId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            orders.add(mapRow(rs));
        }
        return orders;
    }

    public List<Order> getBySellerId(long sellerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE seller_id = ? ORDER BY order_date DESC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, sellerId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            orders.add(mapRow(rs));
        }
        return orders;
    }

    public void updateStatus(long orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, status);
        ps.setLong(2, orderId);
        ps.executeUpdate();
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setCustomerId(rs.getLong("customer_id"));
        order.setProductId(rs.getLong("product_id"));
        order.setSellerId(rs.getLong("seller_id"));
        order.setQuantity(rs.getInt("quantity"));
        order.setUnitPrice(rs.getDouble("unit_price"));
        order.setTotalPrice(rs.getDouble("total_price"));
        order.setStatus(rs.getString("status"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setShippingCity(rs.getString("shipping_city"));
        order.setShippingPostal(rs.getString("shipping_postal"));
        order.setShippingEmail(rs.getString("shipping_email"));
        order.setShippingPhone(rs.getString("shipping_phone"));
        order.setNotes(rs.getString("notes"));
        Timestamp orderDate = rs.getTimestamp("order_date");
        if (orderDate != null) order.setOrderDate(orderDate.toLocalDateTime());
        Date deliveryDate = rs.getDate("delivery_date");
        if (deliveryDate != null) order.setDeliveryDate(deliveryDate.toLocalDate());
        Timestamp cancelledAt = rs.getTimestamp("cancelled_at");
        if (cancelledAt != null) order.setCancelledAt(cancelledAt.toLocalDateTime());
        order.setCancelledReason(rs.getString("cancelled_reason"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) order.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) order.setUpdatedAt(updatedAt.toLocalDateTime());
        return order;
    }
}
