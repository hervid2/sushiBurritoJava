package com.restaurante.app.service;

import com.restaurante.app.database.PedidoDAO;
import com.restaurante.app.database.ProductoDAO;
import com.restaurante.app.models.Categoria;
import com.restaurante.app.models.DetallePedido;
import com.restaurante.app.models.DetallePedidoDTO;
import com.restaurante.app.models.Pedido;
import com.restaurante.app.models.Producto;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Business rules for orders: creation, editing, status changes and the kitchen queue.
 *
 * <p>Free of Swing dependencies. Prototype-scoped so it owns dedicated JDBC connections through its
 * DAOs; callers must invoke {@link #close()} when finished. This bridge disappears with Spring Data
 * JPA in a later iteration.
 */
@Service
@Scope("prototype")
public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final ProductoDAO productoDAO;

    /**
     * @param pedidoDAO   order data-access object injected by Spring
     * @param productoDAO product data-access object injected by Spring
     */
    public PedidoService(PedidoDAO pedidoDAO, ProductoDAO productoDAO) {
        this.pedidoDAO = pedidoDAO;
        this.productoDAO = productoDAO;
    }

    /**
     * Releases the underlying JDBC connections held by this service.
     */
    public void close() {
        pedidoDAO.close();
        productoDAO.close();
    }

    /**
     * Creates a new order together with its line items, marking it as pending so it reaches the
     * kitchen queue.
     *
     * @param pedido            the order header (user, table)
     * @param detallesPedidoDTO the line items entered in the view
     * @param productosResumen  denormalised product summary stored on the order
     * @param categoriasResumen denormalised category summary stored on the order
     * @throws SQLException if persistence fails
     */
    public void crearPedido(Pedido pedido, List<DetallePedidoDTO> detallesPedidoDTO,
                            String productosResumen, String categoriasResumen) throws SQLException {
        pedido.setEstado("pendiente");
        pedidoDAO.insertarPedidoConDetalles(pedido, toDetalles(detallesPedidoDTO),
                productosResumen, categoriasResumen);
    }

    /**
     * Updates an existing order and its line items, rebuilding the product/category summaries from the
     * supplied lines.
     *
     * @param pedido            the order header carrying its id
     * @param detallesPedidoDTO the new line items that replace the previous ones
     * @throws SQLException if persistence fails
     */
    public void actualizarPedido(Pedido pedido, List<DetallePedidoDTO> detallesPedidoDTO) throws SQLException {
        List<DetallePedido> detalles = toDetalles(detallesPedidoDTO);

        StringBuilder productosResumen = new StringBuilder();
        Set<String> categoriasUnicas = new LinkedHashSet<>();
        for (DetallePedidoDTO dto : detallesPedidoDTO) {
            Producto producto = productoDAO.obtenerProductoPorId(dto.getProductoId());
            if (producto != null) {
                if (productosResumen.length() > 0) {
                    productosResumen.append(", ");
                }
                productosResumen.append(dto.getCantidad()).append(" ").append(producto.getNombre());

                Categoria categoria = productoDAO.obtenerCategoriaPorId(producto.getCategoriaId());
                if (categoria != null) {
                    categoriasUnicas.add(categoria.getNombre());
                }
            }
        }

        pedidoDAO.actualizarPedidoConDetalles(pedido, detalles,
                productosResumen.toString(), String.join(", ", categoriasUnicas));
    }

    /**
     * Updates the status of an order.
     *
     * @param pedidoId    the order id
     * @param nuevoEstado the new status (e.g. "cancelado", "entregado", "pagado")
     * @throws SQLException if persistence fails
     */
    public void actualizarEstado(int pedidoId, String nuevoEstado) throws SQLException {
        pedidoDAO.actualizarEstadoPedido(pedidoId, nuevoEstado);
    }

    /**
     * @param pedidoId the order id
     * @return the order, or {@code null} if not found
     * @throws SQLException if the lookup fails
     */
    public Pedido obtenerPedidoPorId(int pedidoId) throws SQLException {
        return pedidoDAO.obtenerPedidoPorId(pedidoId);
    }

    /**
     * @param pedidoId the order id
     * @return the line items belonging to the order
     * @throws SQLException if the lookup fails
     */
    public List<DetallePedido> obtenerDetallesPorPedidoId(int pedidoId) throws SQLException {
        return pedidoDAO.obtenerDetallesPorPedidoId(pedidoId);
    }

    /**
     * @return every order, newest first
     * @throws SQLException if the lookup fails
     */
    public List<Pedido> obtenerTodosLosPedidos() throws SQLException {
        return pedidoDAO.obtenerTodosLosPedidos();
    }

    /**
     * @param estado the status to filter by
     * @return orders in the given status
     * @throws SQLException if the lookup fails
     */
    public List<Pedido> obtenerPedidosPorEstado(String estado) throws SQLException {
        return pedidoDAO.obtenerPedidosPorEstado(estado);
    }

    /**
     * Builds the kitchen queue by combining pending and in-preparation orders and formatting each one
     * for the kitchen table.
     *
     * @return one map per order with keys {@code id}, {@code mesa}, {@code productos},
     *         {@code categorias}, {@code hora} and {@code estado}
     * @throws SQLException if the lookup fails
     */
    public List<Map<String, Object>> obtenerPedidosParaCocina() throws SQLException {
        List<Pedido> relevantes = new ArrayList<>();
        relevantes.addAll(pedidoDAO.obtenerPedidosPorEstado("pendiente"));
        relevantes.addAll(pedidoDAO.obtenerPedidosPorEstado("preparando"));

        List<Map<String, Object>> pedidosFormateados = new ArrayList<>();
        for (Pedido p : relevantes) {
            Map<String, Object> pedidoMap = new HashMap<>();
            pedidoMap.put("id", p.getPedidoId());
            pedidoMap.put("mesa", p.getMesa());
            pedidoMap.put("productos", p.getProductosResumen());
            pedidoMap.put("categorias", p.getCategoriasResumen());

            LocalDateTime horaParaCocina = p.getHoraEntrada();
            if (horaParaCocina == null && p.getFechaCreacion() != null) {
                horaParaCocina = p.getFechaCreacion().toLocalDateTime();
            }
            pedidoMap.put("hora", horaParaCocina);
            pedidoMap.put("estado", p.getEstado());

            pedidosFormateados.add(pedidoMap);
        }
        return pedidosFormateados;
    }

    /**
     * Maps the view-facing line-item DTOs to persistence entities.
     */
    private List<DetallePedido> toDetalles(List<DetallePedidoDTO> detallesPedidoDTO) {
        List<DetallePedido> detalles = new ArrayList<>();
        for (DetallePedidoDTO dto : detallesPedidoDTO) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(dto.getProductoId());
            detalle.setCantidad(dto.getCantidad());
            detalle.setNotas(dto.getNotas());
            detalles.add(detalle);
        }
        return detalles;
    }
}
