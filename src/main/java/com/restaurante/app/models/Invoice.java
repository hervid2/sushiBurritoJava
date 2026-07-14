package com.restaurante.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * The invoice generated for a paid {@link Order}.
 *
 * <p>Mapped to the {@code facturas} table. The monetary amounts are a legitimate accounting snapshot
 * frozen at billing time (kept as-is in Iteration 5). The {@code @Column} overrides map the
 * still-Spanish schema and are dropped when the schema is renamed.
 */
@Entity
@Table(name = "facturas")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "factura_id")
    private Integer id;

    @Column(name = "pedido_id")
    private Integer orderId;

    @Column(name = "subtotal")
    private double subtotal;

    @Column(name = "impuesto_total")
    private double totalTax;

    @Column(name = "propina")
    private double tip;

    @Column(name = "total")
    private double total;

    @Column(name = "fecha_factura")
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
