
package main.java.com.restaurante.app.database;

import main.java.com.restaurante.app.models.Factura;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {

    private final Connection connection;

    public FacturaDAO() throws SQLException {
        this.connection = Conexion.getConnection();
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión de FacturaDAO cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión de FacturaDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertarFactura(Factura factura) throws SQLException {
        // Asegúrate de que los nombres de columna coincidan exactamente con tu tabla.
        // Asumo 'propina' como nombre de columna.
        String sql = "INSERT INTO facturas (pedido_id, subtotal, impuesto_total, propina, total, fecha_factura) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, factura.getPedidoId());
            stmt.setDouble(2, factura.getSubtotal());
            stmt.setDouble(3, factura.getImpuestoTotal());
            stmt.setDouble(4, factura.getPropina()); // ¡Ahora insertamos la propina!
            stmt.setDouble(5, factura.getTotal());
            stmt.setTimestamp(6, Timestamp.valueOf(factura.getFechaFactura()));
            stmt.executeUpdate();
        }
    }

    public Factura obtenerFacturaPorPedidoId(int pedidoId) throws SQLException {
        String sql = "SELECT * FROM facturas WHERE pedido_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapFactura(rs);
            }
        }
        return null;
    }

    public List<Factura> obtenerTodasLasFacturas() throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM facturas";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                facturas.add(mapFactura(rs));
            }
        }
        return facturas;
    }

    public List<Factura> obtenerFacturasConDetalles() throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        // Asumiendo que 'usuario_id', 'estado', 'mesa' se necesitan para la vista de facturas
        // Si no se usan directamente en el modelo Factura, esta query es más para un reporte.
        // Si solo necesitas el pedido_id para el join, una query más simple es suficiente.
        // Aquí no estamos mapeando usuario_id, estado, mesa al modelo Factura directamente.
        String sql = "SELECT f.* FROM facturas f JOIN pedidos p ON f.pedido_id = p.pedido_id"; // Eliminé las columnas de p si no se mapean
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Factura f = mapFactura(rs);
                facturas.add(f);
            }
        }
        return facturas;
    }

    public List<Factura> obtenerFacturasPorRango(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM facturas WHERE fecha_factura BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(desde));
            stmt.setTimestamp(2, Timestamp.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                facturas.add(mapFactura(rs));
            }
        }
        return facturas;
    }

    public String obtenerProductoMasVendido(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        String sql = """
            SELECT p.nombre
            FROM detalle_pedido dp
            JOIN productos p ON dp.producto_id = p.producto_id
            JOIN pedidos pe ON dp.pedido_id = pe.pedido_id
            JOIN facturas f ON pe.pedido_id = f.pedido_id
            WHERE f.fecha_factura BETWEEN ? AND ?
            GROUP BY dp.producto_id
            ORDER BY SUM(dp.cantidad) DESC
            LIMIT 1
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(desde));
            stmt.setTimestamp(2, Timestamp.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("nombre") : null;
        }
    }

    public String obtenerProductoMenosVendido(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        String sql = """
            SELECT p.nombre
            FROM detalle_pedido dp
            JOIN productos p ON dp.producto_id = p.producto_id
            JOIN pedidos pe ON dp.pedido_id = pe.pedido_id
            JOIN facturas f ON pe.pedido_id = f.pedido_id
            WHERE f.fecha_factura BETWEEN ? AND ?
            GROUP BY dp.producto_id
            ORDER BY SUM(dp.cantidad) ASC
            LIMIT 1
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(desde));
            stmt.setTimestamp(2, Timestamp.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("nombre") : null;
        }
    }

    private Factura mapFactura(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setId(rs.getInt("factura_id"));
        f.setPedidoId(rs.getInt("pedido_id"));
        f.setSubtotal(rs.getDouble("subtotal"));
        f.setImpuestoTotal(rs.getDouble("impuesto_total"));
        f.setPropina(rs.getDouble("propina")); // ¡Ahora leemos la propina!
        f.setTotal(rs.getDouble("total"));
        f.setFechaFactura(rs.getTimestamp("fecha_factura").toLocalDateTime());
        return f;
    }
}

