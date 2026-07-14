package com.restaurante.app.service;

import com.restaurante.app.database.FacturaDAO;
import com.restaurante.app.models.Factura;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business rules for invoices: persisting a generated invoice and answering sales queries.
 *
 * <p>Free of Swing dependencies. Prototype-scoped so it owns a dedicated JDBC connection through
 * {@link FacturaDAO}; callers must invoke {@link #close()} when finished.
 */
@Service
@Scope("prototype")
public class FacturaService {

    private final FacturaDAO facturaDAO;

    /**
     * @param facturaDAO invoice data-access object injected by Spring
     */
    public FacturaService(FacturaDAO facturaDAO) {
        this.facturaDAO = facturaDAO;
    }

    /**
     * Releases the underlying JDBC connection held by this service.
     */
    public void close() {
        facturaDAO.close();
    }

    /**
     * Persists an invoice. The monetary amounts are stored as a snapshot at billing time.
     *
     * @param factura the invoice to store
     * @throws SQLException if persistence fails
     */
    public void insertarFactura(Factura factura) throws SQLException {
        facturaDAO.insertarFactura(factura);
    }

    /**
     * @param pedidoId the order id
     * @return the invoice for the order, or {@code null} if it has not been billed
     * @throws SQLException if the lookup fails
     */
    public Factura obtenerFacturaPorPedidoId(int pedidoId) throws SQLException {
        return facturaDAO.obtenerFacturaPorPedidoId(pedidoId);
    }

    /**
     * @param desde inclusive lower bound of the billing date range
     * @param hasta inclusive upper bound of the billing date range
     * @return invoices billed within the range
     * @throws SQLException if the lookup fails
     */
    public List<Factura> obtenerFacturasPorRango(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        return facturaDAO.obtenerFacturasPorRango(desde, hasta);
    }

    /**
     * @return the name of the best-selling product in the range, or {@code null} if there are no sales
     * @throws SQLException if the lookup fails
     */
    public String obtenerNombreProductoMasVendido(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        return facturaDAO.obtenerProductoMasVendido(desde, hasta);
    }

    /**
     * @return the name of the worst-selling product in the range, or {@code null} if there are no sales
     * @throws SQLException if the lookup fails
     */
    public String obtenerNombreProductoMenosVendido(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        return facturaDAO.obtenerProductoMenosVendido(desde, hasta);
    }
}
