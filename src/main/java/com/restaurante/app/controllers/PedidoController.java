package main.java.com.restaurante.app.controllers;

import main.java.com.restaurante.app.database.PedidoDAO;
import main.java.com.restaurante.app.database.ProductoDAO;
import main.java.com.restaurante.app.models.DetallePedido;
import main.java.com.restaurante.app.models.DetallePedidoDTO;
import main.java.com.restaurante.app.models.Pedido;
import main.java.com.restaurante.app.models.Producto;
import main.java.com.restaurante.app.models.Categoria;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PedidoController {
    private final PedidoDAO pedidoDAO;
    private final ProductoDAO productoDAO;

    public PedidoController() throws SQLException {
        this.pedidoDAO = new PedidoDAO();
        this.productoDAO = new ProductoDAO();
    }

    public void closeDAOs() {
        if (pedidoDAO != null) {
            pedidoDAO.close();
        }
        if (productoDAO != null) {
            productoDAO.close();
        }
    }

    public void crearPedido(Pedido pedido, List<DetallePedidoDTO> detallesPedidoDTO, String productosResumen, String categoriasResumen) throws SQLException {
        pedido.setEstado("pendiente"); // Por defecto, los nuevos pedidos son "pendientes"
        List<DetallePedido> detallesParaBD = new ArrayList<>();
        for (DetallePedidoDTO dto : detallesPedidoDTO) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(dto.getProductoId());
            detalle.setCantidad(dto.getCantidad());
            detalle.setNotas(dto.getNotas());
            detallesParaBD.add(detalle);
        }
        pedidoDAO.insertarPedidoConDetalles(pedido, detallesParaBD, productosResumen, categoriasResumen);
    }

    public void actualizarEstado(int pedidoId, String nuevoEstado) throws SQLException {
        pedidoDAO.actualizarEstadoPedido(pedidoId, nuevoEstado);
    }

    public Pedido obtenerPedidoPorId(int pedidoId) throws SQLException {
        return pedidoDAO.obtenerPedidoPorId(pedidoId);
    }

    public List<DetallePedido> obtenerDetallesPorPedidoId(int pedidoId) throws SQLException {
        return pedidoDAO.obtenerDetallesPorPedidoId(pedidoId);
    }

    public void actualizarPedido(Pedido pedido, List<DetallePedidoDTO> detallesPedidoDTO) throws SQLException {
        List<DetallePedido> detallesParaBD = new ArrayList<>();
        StringBuilder productosResumen = new StringBuilder();
        Set<String> categoriasUnicas = new HashSet<>();

        for (DetallePedidoDTO dto : detallesPedidoDTO) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(dto.getProductoId());
            detalle.setCantidad(dto.getCantidad());
            detalle.setNotas(dto.getNotas());
            detallesParaBD.add(detalle);

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
        String categoriasResumenStr = String.join(", ", categoriasUnicas);
        pedidoDAO.actualizarPedidoConDetalles(pedido, detallesParaBD, productosResumen.toString(), categoriasResumenStr);
    }

    public List<Pedido> obtenerTodosLosPedidos() throws SQLException {
        return pedidoDAO.obtenerTodosLosPedidos();
    }


    /**
     * Obtiene pedidos para el panel de cocina.
     * Combina pedidos en estado "pendiente" y "preparando" y los formatea
     * para ser mostrados en la tabla de la cocina.
     * NOTA: Este método ahora asume que las columnas 'producto' y 'producto_categoria'
     * ya están populadas en la tabla 'pedidos' durante la creación del pedido,
     * simplificando la recuperación para la cocina.
     *
     * @return Una lista de Map<String, Object> con los datos del pedido formateados.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Map<String, Object>> obtenerPedidosParaCocina() throws SQLException {
        List<Map<String, Object>> pedidosFormateados = new ArrayList<>();

        List<Pedido> pedidosPendientes = pedidoDAO.obtenerPedidosPorEstado("pendiente");
        List<Pedido> pedidosPreparando = pedidoDAO.obtenerPedidosPorEstado("preparando");

        List<Pedido> todosLosPedidosRelevantes = new ArrayList<>();
        todosLosPedidosRelevantes.addAll(pedidosPendientes);
        todosLosPedidosRelevantes.addAll(pedidosPreparando);

        for (Pedido p : todosLosPedidosRelevantes) {
            Map<String, Object> pedidoMap = new HashMap<>();
            // Asegúrate de que las claves coincidan con las que CocinaPanelView espera
            pedidoMap.put("id", p.getPedidoId()); // AGREGADO o VERIFICADO: Clave "id" para ID Pedido
            pedidoMap.put("mesa", p.getMesa());   // AGREGADO o VERIFICADO: Clave "mesa" para Mesa

            pedidoMap.put("productos", p.getProductosResumen());
            pedidoMap.put("categorias", p.getCategoriasResumen());

            LocalDateTime horaParaCocina = p.getHoraEntrada(); // Usa getHoraEntrada directamente
            if (horaParaCocina == null && p.getFechaCreacion() != null) {
                horaParaCocina = p.getFechaCreacion().toLocalDateTime(); // Fallback si horaEntrada es null
            }
            pedidoMap.put("hora", horaParaCocina); // AGREGADO o VERIFICADO: Clave "hora" para Hora Entrada

            pedidoMap.put("estado", p.getEstado());

            pedidosFormateados.add(pedidoMap);
        }
        return pedidosFormateados;
    }
}