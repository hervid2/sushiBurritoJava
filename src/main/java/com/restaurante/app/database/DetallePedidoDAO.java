package main.java.com.restaurante.app.database;

import main.java.com.restaurante.app.models.DetallePedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAO {

    private final Connection connection;

    public DetallePedidoDAO() throws SQLException {
        this.connection = Conexion.getConnection();
    }

    public void insertarDetalle(DetallePedido detalle) throws SQLException {
        String sql = "INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, notas) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, detalle.getPedidoId());
            stmt.setInt(2, detalle.getProductoId());
            stmt.setInt(3, detalle.getCantidad());
            stmt.setString(4, detalle.getNotas());
            stmt.executeUpdate();
        }
    }

    public List<DetallePedido> obtenerDetallesPorPedido(int pedidoId) throws SQLException {
        List<DetallePedido> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_pedido WHERE pedido_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                detalles.add(mapDetalle(rs));
            }
        }
        return detalles;
    }

    public void eliminarDetallesPorPedido(int pedidoId) throws SQLException {
        String sql = "DELETE FROM detalle_pedido WHERE pedido_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            stmt.executeUpdate();
        }
    }

    private DetallePedido mapDetalle(ResultSet rs) throws SQLException {
        DetallePedido d = new DetallePedido();
        d.setDetalleId(rs.getInt("detalle_id")); 
        d.setPedidoId(rs.getInt("pedido_id"));
        d.setProductoId(rs.getInt("producto_id"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setNotas(rs.getString("notas"));
        return d;
    }
}
