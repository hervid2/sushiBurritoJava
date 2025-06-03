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

    /**
     * Crea un nuevo pedido en la base de datos junto con sus detalles.
     * Ahora recibe los resúmenes de productos y categorías para guardarlos directamente en el pedido.
     *
     * @param pedido                  Objeto Pedido con los datos principales (usuarioId, mesa).
     * Nota: El ID del pedido se generará en el DAO.
     * @param detallesPedidoDTO       Lista de DTOs de detalles del pedido desde la vista.
     * @param productosResumen        Cadena de texto con el resumen de productos (ej. "2 Sushi Roll, 1 Ramen").
     * @param categoriasResumen       Cadena de texto con el resumen de categorías (ej. "Sushi, Platos Fuertes").
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void crearPedido(Pedido pedido, List<DetallePedidoDTO> detallesPedidoDTO, String productosResumen, String categoriasResumen) throws SQLException {
        // Establecer el estado inicial del pedido. ¡Esto es CRUCIAL para que aparezca en CocinaPanelView!
        pedido.setEstado("pendiente"); // Por defecto, los nuevos pedidos son "pendientes"

        // Convertir la lista de DetallePedidoDTO a DetallePedido para el DAO
        List<DetallePedido> detallesParaBD = new ArrayList<>();
        for (DetallePedidoDTO dto : detallesPedidoDTO) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(dto.getProductoId());
            detalle.setCantidad(dto.getCantidad());
            detalle.setNotas(dto.getNotas());
            detallesParaBD.add(detalle);
        }

        // Ahora llama al DAO con todos los argumentos requeridos, incluyendo los resúmenes
        pedidoDAO.insertarPedidoConDetalles(pedido, detallesParaBD, productosResumen, categoriasResumen);
    }

    /**
     * Actualiza el estado de un pedido específico.
     * @param pedidoId El ID del pedido a actualizar.
     * @param nuevoEstado El nuevo estado del pedido (ej. "cancelado", "entregado", "pagado").
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void actualizarEstado(int pedidoId, String nuevoEstado) throws SQLException {
        pedidoDAO.actualizarEstadoPedido(pedidoId, nuevoEstado);
    }

    /**
     * Obtiene un pedido completo por su ID.
     * @param pedidoId El ID del pedido a obtener.
     * @return El objeto Pedido si se encuentra, null si no.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public Pedido obtenerPedidoPorId(int pedidoId) throws SQLException {
        return pedidoDAO.obtenerPedidoPorId(pedidoId);
    }

    /**
     * Obtiene la lista de detalles para un pedido específico.
     * Este método es llamado por la vista de edición para cargar los productos de un pedido existente.
     * @param pedidoId El ID del pedido del cual obtener los detalles.
     * @return Una lista de objetos DetallePedido.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<DetallePedido> obtenerDetallesPorPedidoId(int pedidoId) throws SQLException {
        return pedidoDAO.obtenerDetallesPorPedidoId(pedidoId);
    }

    /**
     * Actualiza un pedido existente y sus detalles.
     * Este método es el que la vista de edición llamaría para guardar los cambios.
     * Genera los resúmenes de productos y categorías a partir de los detalles para guardarlos en el pedido principal.
     *
     * @param pedido                  Objeto Pedido con los datos actualizados (debe tener el pedidoId).
     * @param detallesPedidoDTO       Lista de DTOs de detalles del pedido desde la vista (los nuevos detalles).
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void actualizarPedido(Pedido pedido, List<DetallePedidoDTO> detallesPedidoDTO) throws SQLException {
        // Convertir la lista de DetallePedidoDTO a DetallePedido para el DAO
        List<DetallePedido> detallesParaBD = new ArrayList<>();
        StringBuilder productosResumen = new StringBuilder();
        Set<String> categoriasUnicas = new HashSet<>();

        // Para construir los resúmenes, necesitamos los nombres de los productos y categorías.
        // Asumo que ProductoDAO tiene métodos para obtener Producto y Categoria por ID.
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

        // Llama al DAO para actualizar el pedido y sus detalles
        pedidoDAO.actualizarPedidoConDetalles(pedido, detallesParaBD, productosResumen.toString(), categoriasResumenStr);
    }

    /**
     * Obtiene todos los pedidos de la base de datos.
     * Este método es usado por vistas para cargar una lista completa de pedidos.
     * @return Una lista de objetos Pedido.
     * @throws SQLException Si ocurre un error de base de datos.
     */
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
            pedidoMap.put("id", p.getPedidoId()); // Clave "id" para ID Pedido
            pedidoMap.put("mesa", p.getMesa());   // Clave "mesa" para Mesa

            pedidoMap.put("productos", p.getProductosResumen());
            pedidoMap.put("categorias", p.getCategoriasResumen());

            LocalDateTime horaParaCocina = p.getHoraEntrada(); // Usa getHoraEntrada directamente
            if (horaParaCocina == null && p.getFechaCreacion() != null) {
                horaParaCocina = p.getFechaCreacion().toLocalDateTime(); // Fallback si horaEntrada es null
            }
            pedidoMap.put("hora", horaParaCocina); // Clave "hora" para Hora Entrada

            pedidoMap.put("estado", p.getEstado()); // Clave "estado" para el estado del pedido

            pedidosFormateados.add(pedidoMap);
        }
        return pedidosFormateados;
    }
}