package com.restaurante.app.models;

/**
 * View-facing line item captured while composing an order, before it becomes an {@link OrderItem}.
 */
public class OrderItemDTO {

    private int productId;
    private int quantity;
    private String notes;

    public OrderItemDTO() {
    }

    public OrderItemDTO(int productId, int quantity, String notes) {
        this.productId = productId;
        this.quantity = quantity;
        this.notes = notes;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
