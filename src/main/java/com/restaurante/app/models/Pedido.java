package main.java.com.restaurante.app.models;

import java.sql.Timestamp;
import java.time.LocalDateTime; // Importar para horaEntrada

public class Pedido {
    private int pedidoId;
    private int usuarioId; // ID del mesero/usuario que tomó el pedido
    private int mesa;
    private String estado; // Ej: "pendiente", "preparando", "entregado", "cancelado"
    private java.sql.Timestamp fechaCreacion; // O Date
    private java.sql.Timestamp fechaModificacion; // O Date (para saber cuándo se editó por última vez)

    // --- Nuevos atributos para los resúmenes de productos/categorías y la hora de entrada ---
    private String productosResumen;    // Para la columna 'producto' en la DB
    private String categoriasResumen;   // Para la columna 'producto_categoria' en la DB
    private LocalDateTime horaEntrada;  // Para la columna 'hora_entrada' en la DB


    public Pedido() {}

    public Pedido(int pedidoId, int usuarioId, int mesa, String estado, Timestamp fechaCreacion, Timestamp fechaModificacion) {
        this.pedidoId = pedidoId;
        this.usuarioId = usuarioId;
        this.mesa = mesa;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
        // No se incluyen los nuevos campos aquí si este constructor es solo para datos iniciales
        // Si necesitas un constructor completo, lo puedes añadir.
    }

    // --- Getters y Setters existentes ---
    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public int getMesa() { return mesa; }
    public void setMesa(int mesa) { this.mesa = mesa; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public java.sql.Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(java.sql.Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public java.sql.Timestamp getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(java.sql.Timestamp fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    // --- Nuevos Getters y Setters para los nuevos atributos ---
    public String getProductosResumen() {
        return productosResumen;
    }

    public void setProductosResumen(String productosResumen) {
        this.productosResumen = productosResumen;
    }

    public String getCategoriasResumen() {
        return categoriasResumen;
    }

    public void setCategoriasResumen(String categoriasResumen) {
        this.categoriasResumen = categoriasResumen;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }


    // Crucial para JComboBox<Pedido>
    @Override
    public String toString() {
        return "Pedido #" + pedidoId + " (Mesa " + mesa + " - " + estado + ")";
    }
}