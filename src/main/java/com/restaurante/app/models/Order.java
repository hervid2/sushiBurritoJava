package com.restaurante.app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * A customer order taken by a waiter.
 *
 * <p>Mapped to the {@code orders} table. The waiter is kept as a plain foreign-key column
 * ({@link #userId}); the line items live in {@link OrderItem}. The denormalised product/category
 * text summaries were removed in Iteration 5 and are now reconstructed from the line items by the
 * {@code v_order_summary} database view (see {@link OrderSummary}). Column names map 1:1 to the
 * fields (snake_case), so no {@code @Column} overrides are needed.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer tableNumber;

    private String status;

    /** When the order was taken; also serves as the kitchen entry time (former {@code hora_entrada}). */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Order() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Rendered in {@code JComboBox<Order>}; the label stays in Spanish to keep the UI unchanged.
     */
    @Override
    public String toString() {
        return "Pedido #" + id + " (Mesa " + tableNumber + " - " + status + ")";
    }
}
