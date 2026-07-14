package com.restaurante.app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * Read-only projection backed by the {@code v_order_summary} database view.
 *
 * <p>Replaces the denormalised {@code producto}/{@code producto_categoria} columns removed from the
 * {@code orders} table in Iteration 5: the view rebuilds, per order, a human-readable product summary
 * (e.g. {@code "2 California Roll, 1 Burrito Teriyaki"}) and the distinct category list from the
 * order's line items, keeping {@link OrderItem} the single source of truth.
 */
@Entity
@Immutable
@Table(name = "v_order_summary")
public class OrderSummary {

    @Id
    private Integer orderId;

    private String productSummary;

    private String categorySummary;

    public OrderSummary() {
    }

    public Integer getOrderId() {
        return orderId;
    }

    public String getProductSummary() {
        return productSummary;
    }

    public String getCategorySummary() {
        return categorySummary;
    }
}
