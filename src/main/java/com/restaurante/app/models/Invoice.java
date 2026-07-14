package com.restaurante.app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * The invoice generated for a paid {@link Order}.
 *
 * <p>Mapped to the {@code invoices} table. The monetary amounts are a legitimate accounting snapshot
 * frozen at billing time (kept as-is). Column names map 1:1 to the fields (snake_case), so no
 * {@code @Column} overrides are needed after the Iteration 5 rename.
 */
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer orderId;

    private double subtotal;

    private double totalTax;

    private double tip;

    private double total;

    private LocalDateTime invoicedAt;

    public Invoice() {
    }

    public Invoice(Integer id, Integer orderId, double subtotal, double totalTax, double tip,
                   double total, LocalDateTime invoicedAt) {
        this.id = id;
        this.orderId = orderId;
        this.subtotal = subtotal;
        this.totalTax = totalTax;
        this.tip = tip;
        this.total = total;
        this.invoicedAt = invoicedAt;
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

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(double totalTax) {
        this.totalTax = totalTax;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getInvoicedAt() {
        return invoicedAt;
    }

    public void setInvoicedAt(LocalDateTime invoicedAt) {
        this.invoicedAt = invoicedAt;
    }
}
