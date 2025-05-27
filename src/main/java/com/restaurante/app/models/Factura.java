package main.java.com.restaurante.app.models;

import java.time.LocalDateTime;

public class Factura {
    private int id;
    private int pedidoId;
    private double subtotal;
    private double impuestoTotal;
    private double total;
    private LocalDateTime fechaFactura;

    public Factura() {
    }

    public Factura(int id, int pedidoId, double subtotal, double impuestoTotal, double total, LocalDateTime fechaFactura) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.subtotal = subtotal;
        this.impuestoTotal = impuestoTotal;
        this.total = total;
        this.fechaFactura = fechaFactura;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getImpuestoTotal() {
        return impuestoTotal;
    }

    public void setImpuestoTotal(double impuestoTotal) {
        this.impuestoTotal = impuestoTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(LocalDateTime fechaFactura) {
        this.fechaFactura = fechaFactura;
    }
}
