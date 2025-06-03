package main.java.com.restaurante.app.models;

// Asegúrate de tener todas las importaciones necesarias si hay más atributos
// Por ejemplo, si usas LocalDateTime: import java.time.LocalDateTime;

public class DetallePedido {
    private int detalleId; // <-- Añade este atributo
    private int pedidoId;
    private int productoId;
    private int cantidad;
    private String notas;

    // Constructor vacío (si lo necesitas)
    public DetallePedido() {
    }

    // Constructor con todos los atributos (opcional, pero útil)
    public DetallePedido(int detalleId, int pedidoId, int productoId, int cantidad, String notas) {
        this.detalleId = detalleId;
        this.pedidoId = pedidoId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.notas = notas;
    }

    // Getters y Setters
    // Getter para detalleId
    public int getDetalleId() { // <-- Añade este método
        return detalleId;
    }

    // Setter para detalleId
    public void setDetalleId(int detalleId) { // <-- ¡Añade este método!
        this.detalleId = detalleId;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    // Opcional: toString() para depuración
    @Override
    public String toString() {
        return "DetallePedido{" +
               "detalleId=" + detalleId +
               ", pedidoId=" + pedidoId +
               ", productoId=" + productoId +
               ", cantidad=" + cantidad +
               ", notas='" + notas + '\'' +
               '}';
    }
}
