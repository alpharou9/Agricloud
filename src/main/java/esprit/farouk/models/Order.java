package esprit.farouk.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Order {
    private long id;
    private long customerId;
    private long productId;
    private long sellerId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String status; // pending, confirmed, processing, shipped, delivered, cancelled
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostal;
    private String notes;
    private LocalDateTime orderDate;
    private LocalDate deliveryDate;
    private LocalDateTime cancelledAt;
    private String cancelledReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order() {}

    public Order(long customerId, long productId, long sellerId, int quantity, double unitPrice, String shippingAddress) {
        this.customerId = customerId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice * quantity;
        this.shippingAddress = shippingAddress;
        this.status = "pending";
        this.orderDate = LocalDateTime.now();
    }

    public Order(long id, long customerId, long productId, long sellerId, int quantity,
                 double unitPrice, double totalPrice, String status, String shippingAddress,
                 String shippingCity, String shippingPostal, String notes, LocalDateTime orderDate,
                 LocalDate deliveryDate, LocalDateTime cancelledAt, String cancelledReason,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.shippingCity = shippingCity;
        this.shippingPostal = shippingPostal;
        this.notes = notes;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.cancelledAt = cancelledAt;
        this.cancelledReason = cancelledReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public long getSellerId() { return sellerId; }
    public void setSellerId(long sellerId) { this.sellerId = sellerId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }

    public String getShippingPostal() { return shippingPostal; }
    public void setShippingPostal(String shippingPostal) { this.shippingPostal = shippingPostal; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancelledReason() { return cancelledReason; }
    public void setCancelledReason(String cancelledReason) { this.cancelledReason = cancelledReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", productId=" + productId + ", quantity=" + quantity + ", status='" + status + "'}";
    }
}
