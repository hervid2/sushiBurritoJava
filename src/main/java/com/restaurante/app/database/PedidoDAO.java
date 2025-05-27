package main.java.com.restaurante.app.database;

import main.java.com.restaurante.app.models.Pedido;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private final Connection connection;

    public PedidoDAO() throws SQLException {
        this.connection = Conexion.getConnection();
    }

    public void insertarPedido(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (usuario_id, mesa, estado, fecha_creacion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pedido.getUsuarioId());
            stmt.setInt(2, pedido.getMesa());
            stmt.setString(3, pedido.getEstado());
            stmt.setTimestamp(4, Timestamp.valueOf(pedido.getFechaCreacion()));
            stmt.executeUpdate();
        }
    }

    public Pedido obtenerPedidoPorId(int id) throws SQLException {
        String sql = "SELECT * FROM pedidos WHERE pedido_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapPedido(rs);
            }
        }
        return null;
    }

    public List<Pedido> obtenerTodosLosPedidos() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pedidos.add(mapPedido(rs));
            }
        }
        return pedidos;
    }

    public void actualizarPedido(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET usuario_id = ?, mesa = ?, estado = ?, fecha_creacion = ? WHERE pedido_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pedido.getUsuarioId());
            stmt.setInt(2, pedido.getMesa());
            stmt.setString(3, pedido.getEstado());
            stmt.setTimestamp(4, Timestamp.valueOf(pedido.getFechaCreacion()));
            stmt.setInt(5, pedido.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminarPedido(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE pedido_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Pedido mapPedido(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("pedido_id"));
        p.setUsuarioId(rs.getInt("usuario_id"));
        p.setMesa(rs.getInt("mesa"));
        p.setEstado(rs.getString("estado"));
        p.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        return p;
    }
}