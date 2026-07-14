package com.restaurante.app.service;

import com.restaurante.app.models.EstadisticaProductoDTO;
import com.restaurante.app.models.Factura;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Produces the sales statistics shown on the admin dashboard (revenue, best/worst selling products).
 *
 * <p>Free of Swing dependencies. Delegates data access to {@link FacturaService}; prototype-scoped so
 * the underlying JDBC connection is released through {@link #close()}.
 */
@Service
@Scope("prototype")
public class EstadisticaService {

    private final FacturaService facturaService;

    /**
     * @param facturaService invoice service supplying the underlying sales data
     */
    public EstadisticaService(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    /**
     * Releases the underlying JDBC connection held by this service.
     */
    public void close() {
        facturaService.close();
    }

    /**
     * @return invoices billed between the two instants
     * @throws SQLException if the lookup fails
     */
    public List<Factura> obtenerFacturasEntreFechas(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        return facturaService.obtenerFacturasPorRango(desde, hasta);
    }

    /**
     * @return the best-selling product for the range, or an {@code N/A} placeholder when there are no sales
     * @throws SQLException if the lookup fails
     */
    public EstadisticaProductoDTO obtenerProductoMasVendido(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        String nombre = facturaService.obtenerNombreProductoMasVendido(desde, hasta);
        return new EstadisticaProductoDTO(nombre != null ? nombre : "N/A", "más vendido");
    }

    /**
     * @return the worst-selling product for the range, or an {@code N/A} placeholder when there are no sales
     * @throws SQLException if the lookup fails
     */
    public EstadisticaProductoDTO obtenerProductoMenosVendido(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        String nombre = facturaService.obtenerNombreProductoMenosVendido(desde, hasta);
        return new EstadisticaProductoDTO(nombre != null ? nombre : "N/A", "menos vendido");
    }

    /**
     * Aggregates the dashboard figures for the given date range.
     *
     * @return a map with keys {@code facturas}, {@code totalIngresos}, {@code masVendido} and
     *         {@code menosVendido}
     * @throws SQLException if any lookup fails
     */
    public Map<String, Object> generarEstadisticas(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        List<Factura> facturas = obtenerFacturasEntreFechas(desde, hasta);
        double totalIngresos = facturas.stream().mapToDouble(Factura::getTotal).sum();

        Map<String, Object> datos = new HashMap<>();
        datos.put("facturas", facturas);
        datos.put("totalIngresos", totalIngresos);
        datos.put("masVendido", obtenerProductoMasVendido(desde, hasta));
        datos.put("menosVendido", obtenerProductoMenosVendido(desde, hasta));
        return datos;
    }
}
