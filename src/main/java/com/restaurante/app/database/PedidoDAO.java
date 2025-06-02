package main.java.com.restaurante.app.database;

import main.java.com.restaurante.app.models.Pedido;
import main.java.com.restaurante.app.models.DetallePedido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private final Connection connection;

    public PedidoDAO() throws SQLException {
        this.connection = Conexion.getConnection();
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión de PedidoDAO cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión de PedidoDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserta un nuevo pedido principal junto con sus detalles.
     * Ahora incluye los resúmenes de productos y categorías directamente en la tabla de pedidos.
     * @param pedido Objeto Pedido con los datos principales (usuarioId, mesa, estado).
     * @param detalles Lista de DetallePedido que componen la orden.
     * @param productosResumen Cadena de texto con el resumen de productos (ej. "2 Sushi Roll, 1 Ramen").
     * @param categoriasResumen Cadena de texto con el resumen de categorías (ej. "Sushi, Platos Fuertes").
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void insertarPedidoConDetalles(Pedido pedido, List<DetallePedido> detalles, String productosResumen, String categoriasResumen) throws SQLException {
        // CORREGIDO: Usar 'detalle_pedido' (singular) según la estructura de tu DB
        String sqlInsertPedido = "INSERT INTO pedidos (usuario_id, mesa, estado, fecha_creacion, fecha_modificacion, producto, producto_categoria, hora_entrada) VALUES (?, ?, ?, NOW(), NOW(), ?, ?, NOW())";
        String sqlInsertDetalle = "INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, notas) VALUES (?, ?, ?, ?)"; //

        try {
            connection.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar el pedido principal
            try (PreparedStatement pstmtPedido = connection.prepareStatement(sqlInsertPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPedido.setInt(1, pedido.getUsuarioId());
                pstmtPedido.setInt(2, pedido.getMesa());
                pstmtPedido.setString(3, pedido.getEstado());
                pstmtPedido.setString(4, productosResumen);
                pstmtPedido.setString(5, categoriasResumen);

                pstmtPedido.executeUpdate();

                try (ResultSet generatedKeys = pstmtPedido.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int pedidoId = generatedKeys.getInt(1);
                        pedido.setPedidoId(pedidoId);
                    } else {
                        throw new SQLException("No se pudo obtener el ID generado para el pedido.");
                    }
                }
            }

            // 2. Insertar los detalles del pedido
            try (PreparedStatement pstmtDetalle = connection.prepareStatement(sqlInsertDetalle)) {
                for (DetallePedido detalle : detalles) {
                    pstmtDetalle.setInt(1, pedido.getPedidoId());
                    pstmtDetalle.setInt(2, detalle.getProductoId());
                    pstmtDetalle.setInt(3, detalle.getCantidad());
                    pstmtDetalle.setString(4, detalle.getNotas());
                    pstmtDetalle.addBatch();
                }
                pstmtDetalle.executeBatch();
            }

            connection.commit(); // Confirmar transacción
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Revertir si hay error
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Restaurar auto-commit
            }
        }
    }

    /**
     * Actualiza el estado de un pedido específico.
     * @param pedidoId El ID del pedido a actualizar.
     * @param nuevoEstado El nuevo estado del pedido (ej. "cancelado", "entregado", "pagado").
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void actualizarEstadoPedido(int pedidoId, String nuevoEstado) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ?, fecha_modificacion = NOW() WHERE pedido_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, pedidoId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Actualiza un pedido existente (sus datos principales) y sus detalles asociados.
     * Elimina los detalles antiguos y luego inserta los nuevos.
     * También actualiza los campos de resumen de productos y categorías.
     * @param pedido Objeto Pedido con los datos actualizados (debe tener pedidoId).
     * @param nuevosDetalles Lista de DetallePedido que reemplazará a los existentes.
     * @param productosResumen Cadena de texto con el resumen de productos (ej. "2 Sushi Roll, 1 Ramen").
     * @param categoriasResumen Cadena de texto con el resumen de categorías (ej. "Sushi, Platos Fuertes").
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void actualizarPedidoConDetalles(Pedido pedido, List<DetallePedido> nuevosDetalles, String productosResumen, String categoriasResumen) throws SQLException {
        connection.setAutoCommit(false); // Iniciar transacción

        try {
            // 1. Actualizar los campos del pedido principal
            String updatePedidoSql = "UPDATE pedidos SET mesa = ?, estado = ?, fecha_modificacion = CURRENT_TIMESTAMP, producto = ?, producto_categoria = ? WHERE pedido_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updatePedidoSql)) {
                stmt.setInt(1, pedido.getMesa());
                stmt.setString(2, pedido.getEstado());
                stmt.setString(3, productosResumen);
                stmt.setString(4, categoriasResumen);
                stmt.setInt(5, pedido.getPedidoId());
                stmt.executeUpdate();
            }

            // 2. Eliminar todos los detalles existentes para este pedido (enfoque de "borrar y recrear")
            // CORREGIDO: Usar 'detalle_pedido' (singular)
            String deleteDetallesSql = "DELETE FROM detalle_pedido WHERE pedido_id = ?"; //
            try (PreparedStatement stmt = connection.prepareStatement(deleteDetallesSql)) {
                stmt.setInt(1, pedido.getPedidoId());
                stmt.executeUpdate();
            }

            // 3. Insertar los nuevos detalles
            // CORREGIDO: Usar 'detalle_pedido' (singular)
            String insertDetalleSql = "INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, notas) VALUES (?, ?, ?, ?)"; //
            try (PreparedStatement stmt = connection.prepareStatement(insertDetalleSql)) {
                for (DetallePedido detalle : nuevosDetalles) {
                    stmt.setInt(1, pedido.getPedidoId());
                    stmt.setInt(2, detalle.getProductoId());
                    stmt.setInt(3, detalle.getCantidad());
                    stmt.setString(4, detalle.getNotas());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            connection.commit(); // Confirmar la transacción
        } catch (SQLException e) {
            connection.rollback(); // Revertir si hay un error
            throw e;
        } finally {
            connection.setAutoCommit(true); // Restaurar auto-commit
        }
    }


    /**
     * Obtiene una lista de detalles de pedido para un pedido específico.
     * @param pedidoId El ID del pedido.
     * @return Una lista de objetos DetallePedido.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<DetallePedido> obtenerDetallesPorPedidoId(int pedidoId) throws SQLException {
        List<DetallePedido> detalles = new ArrayList<>();
        // CORREGIDO: Usar 'detalle_pedido' (singular)
        String sql = "SELECT detalle_id, pedido_id, producto_id, cantidad, notas FROM detalle_pedido WHERE pedido_id = ?"; //
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setDetalleId(rs.getInt("detalle_id"));
                    detalle.setPedidoId(rs.getInt("pedido_id"));
                    detalle.setProductoId(rs.getInt("producto_id"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setNotas(rs.getString("notas"));
                    detalles.add(detalle);
                }
            }
        }
        return detalles;
    }


    /**
     * Obtiene todos los pedidos. Ahora selecciona también 'producto', 'producto_categoria', 'hora_entrada'.
     * @return Una lista de objetos Pedido.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Pedido> obtenerTodosLosPedidos() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        // Asegúrate de seleccionar las nuevas columnas
        String sql = "SELECT pedido_id, usuario_id, mesa, estado, fecha_creacion, fecha_modificacion, producto, producto_categoria, hora_entrada FROM pedidos ORDER BY pedido_id DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                pedidos.add(mapPedido(rs));
            }
        }
        return pedidos;
    }

    /**
     * Obtiene un pedido por su ID. Ahora selecciona también 'producto', 'producto_categoria', 'hora_entrada'.
     * @param pedidoId El ID del pedido.
     * @return El objeto Pedido si se encuentra, de lo contrario, null.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public Pedido obtenerPedidoPorId(int pedidoId) throws SQLException {
        String sql = "SELECT pedido_id, usuario_id, mesa, estado, fecha_creacion, fecha_modificacion, producto, producto_categoria, hora_entrada FROM pedidos WHERE pedido_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, pedidoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapPedido(rs);
            }
        }
        return null;
    }

    /**
     * Obtiene una lista de pedidos filtrados por estado. Ahora selecciona también 'producto', 'producto_categoria', 'hora_entrada'.
     * @param estado El estado por el que filtrar (ej. "pendiente", "preparando", "entregado").
     * @return Lista de pedidos que coinciden con el estado.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Pedido> obtenerPedidosPorEstado(String estado) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT pedido_id, usuario_id, mesa, estado, fecha_creacion, fecha_modificacion, producto, producto_categoria, hora_entrada FROM pedidos WHERE estado = ? ORDER BY pedido_id ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, estado);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pedidos.add(mapPedido(rs));
            }
        }
        return pedidos;
    }

    // --- Métodos de mapeo ---
    private Pedido mapPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setPedidoId(rs.getInt("pedido_id"));
        pedido.setUsuarioId(rs.getInt("usuario_id"));
        pedido.setMesa(rs.getInt("mesa"));
        pedido.setEstado(rs.getString("estado"));

        // Obtener Timestamp y convertir a LocalDateTime para la vista de cocina
        Timestamp fechaCreacionTimestamp = rs.getTimestamp("fecha_creacion");
        if (fechaCreacionTimestamp != null) {
            pedido.setFechaCreacion(fechaCreacionTimestamp);
        }

        Timestamp fechaModificacionTimestamp = rs.getTimestamp("fecha_modificacion");
        if (fechaModificacionTimestamp != null) {
            pedido.setFechaModificacion(fechaModificacionTimestamp);
        }

        // Nuevas columnas de resumen
        pedido.setProductosResumen(rs.getString("producto"));
        pedido.setCategoriasResumen(rs.getString("producto_categoria"));

        // Obtener Timestamp de hora_entrada y convertir a LocalDateTime
        Timestamp horaEntradaTimestamp = rs.getTimestamp("hora_entrada");
        if (horaEntradaTimestamp != null) {
            pedido.setHoraEntrada(horaEntradaTimestamp.toLocalDateTime());
        }

        return pedido;
    }
}