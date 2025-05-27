package main.java.com.restaurante.app.models;

public class Producto {
    private int id;
    private String nombre;
    private String ingredientes;
    private double valorNeto;
    private double valorVenta;
    private double impuesto;
    private int categoriaId;

    public Producto() {
    }

    public Producto(int id, String nombre, String ingredientes, double valorNeto, double valorVenta, double impuesto, int categoriaId) {
        this.id = id;
        this.nombre = nombre;
        this.ingredientes = ingredientes;
        this.valorNeto = valorNeto;
        this.valorVenta = valorVenta;
        this.impuesto = impuesto;
        this.categoriaId = categoriaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public double getValorNeto() {
        return valorNeto;
    }

    public void setValorNeto(double valorNeto) {
        this.valorNeto = valorNeto;
    }

    public double getValorVenta() {
        return valorVenta;
    }

    public void setValorVenta(double valorVenta) {
        this.valorVenta = valorVenta;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }
}