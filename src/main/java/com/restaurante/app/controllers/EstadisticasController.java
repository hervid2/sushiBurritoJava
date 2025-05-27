package main.java.com.restaurante.app.controllers;

import main.java.com.restaurante.app.database.FacturaDAO;
import main.java.com.restaurante.app.models.EstadisticaProductoDTO;
import main.java.com.restaurante.app.models.Factura;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadisticasController {

    private final FacturaDAO facturaDAO;

    public EstadisticasController() throws SQLException {
        this.facturaDAO = new FacturaDAO();
    }

    public List<Factura> obtenerFacturasEntreFechas(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        return facturaDAO.obtenerFacturasPorRango(desde, hasta);
    }

    public EstadisticaProductoDTO obtenerProductoMasVendido(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        String nombre = facturaDAO.obtenerProductoMasVendido(desde, hasta);
        return nombre != null ? new EstadisticaProductoDTO(nombre, "más vendido") : new EstadisticaProductoDTO("N/A", "más vendido");
    }

    public EstadisticaProductoDTO obtenerProductoMenosVendido(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        String nombre = facturaDAO.obtenerProductoMenosVendido(desde, hasta);
        return nombre != null ? new EstadisticaProductoDTO(nombre, "menos vendido") : new EstadisticaProductoDTO("N/A", "menos vendido");
    }

    public Map<String, Object> generarEstadisticas(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        Map<String, Object> datos = new HashMap<>();

        List<Factura> facturas = obtenerFacturasEntreFechas(desde, hasta);
        double totalIngresos = facturas.stream().mapToDouble(Factura::getTotal).sum();

        datos.put("facturas", facturas);
        datos.put("totalIngresos", totalIngresos);
        datos.put("masVendido", obtenerProductoMasVendido(desde, hasta));
        datos.put("menosVendido", obtenerProductoMenosVendido(desde, hasta));

        return datos;
    }
} 

