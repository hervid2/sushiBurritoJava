package com.restaurante.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A single line of an {@link Order} (a product, a quantity and optional notes).
 *
 * <p>Mapped to the {@code detalle_pedido} table. The parent order and the product are kept as plain
 * foreign-key columns to match the existing view logic; the {@code @Column} overrides map the
 * still-Spanish schema and are dropped in Iteration 5.
 */
@Entity
@Table(name = "detalle_pedido")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_id")
    private Integer id;

    @Column(name = "pedido_id")
    private Integer orderId;

    @Column(name = "producto_id")
    private Integer productId;

    @Column(name = "cantidad")
    private Integer quantity;

    @Column(name = "notas")
    private String notes;

    public OrderItem() {
    }

    public OrderItem(Integer id, Integer orderId, Integer productId, Integer quantity, String notes) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
