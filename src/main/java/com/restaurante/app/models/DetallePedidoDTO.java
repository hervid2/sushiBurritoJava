package main.java.com.restaurante.app.models;

public class DetallePedidoDTO {
    private int productoId; // Cambiado de String producto a int productoId
    // private String categoria; // Esta línea ya no es necesaria si solo usas el ID del producto
    private int cantidad;
    private String notas;

    // Constructor vacío (si lo necesitas)
    public DetallePedidoDTO() {
    }

    // Este es el constructor que GenerarComandaView espera y necesita
    public DetallePedidoDTO(int productoId, int cantidad, String notas) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.notas = notas;
    }


    // --- Getters y Setters  ---

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
}