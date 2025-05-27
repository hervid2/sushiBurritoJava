package main.java.com.restaurante.app.models;

public class EstadisticaProductoDTO {
    private String nombreProducto;
    private String descripcion; // "más vendido" o "menos vendido"

    public EstadisticaProductoDTO(String nombreProducto, String descripcion) {
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
