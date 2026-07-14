package com.restaurante.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * A customer order taken by a waiter.
 *
 * <p>Mapped to the {@code pedidos} table. The waiter is kept as a plain foreign-key column
 * ({@link #userId}); the line items live in {@link OrderItem}. The {@link #productSummary} and
 * {@link #categorySummary} columns are the denormalised text summaries flagged for removal in
 * Iteration 5. The {@code @Column} overrides map the still-Spanish schema and are dropped there too.
 */
@Entity
@Table(name = "pedidos")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    private Integer id;

    @Column(name = "usuario_id")
    private Integer userId;

    @Column(name = "mesa")
    private Integer tableNumber;

    @Column(name = "estado")
    private String status;

    @Column(name = "fecha_creacion")
    private LocalDateTime createdAt;

    @Column(name = "fecha_modificacion")
    private LocalDateTime updatedAt;

    @Column(name = "producto")
    private String productSummary;

    @Column(name = "producto_categoria")
    private String categorySummary;

    @Column(name = "hora_entrada")
    private LocalDateTime entryTime;

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

    public String getProductSummary() {
        return productSummary;
    }

    public void setProductSummary(String productSummary) {
        this.productSummary = productSummary;
    }

    public String getCategorySummary() {
        return categorySummary;
    }

    public void setCategorySummary(String categorySummary) {
        this.categorySummary = categorySummary;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    /**
     * Rendered in {@code JComboBox<Order>}; the label stays in Spanish to keep the UI unchanged.
     */
    @Override
    public String toString() {
        return "Pedido #" + id + " (Mesa " + tableNumber + " - " + status + ")";
    }
}
