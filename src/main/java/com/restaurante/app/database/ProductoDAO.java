package main.java.com.restaurante.app.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.java.com.restaurante.app.models.Producto;

public class ProductoDAO {

    private final Connection connection;

    /**
     * Constructor de ProductoDAO.
     * Obtiene la conexión a la base de datos.
     * @throws SQLException Si ocurre un error al obtener la conexión.
     */
    public ProductoDAO() throws SQLException {
        this.connection = Conexion.getConnection();
    }

    /**
     * Inserta un nuevo producto en la tabla 'productos'.
     * @param producto El objeto Producto a insertar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void insertarProducto(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, ingredientes, valor_neto, valor_venta, impuesto, categoria_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getIngredientes());
            stmt.setDouble(3, producto.getValorNeto());
            stmt.setDouble(4, producto.getValorVenta());
            stmt.setDouble(5, producto.getImpuesto());
            stmt.setInt(6, producto.getCategoriaId()); // Manejo del categoria_id
            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene un producto de la tabla 'productos' por su ID.
     * @param id El ID del producto a buscar.
     * @return El objeto Producto si se encuentra, de lo contrario, null.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public Producto obtenerProductoPorId(int id) throws SQLException {
        String sql = "SELECT * FROM productos WHERE producto_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapProducto(rs);
            }
        }
        return null;
    }

    /**
     * Obtiene una lista de todos los productos de la tabla 'productos'.
     * @return Una lista de objetos Producto.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Producto> obtenerTodosLosProductos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                productos.add(mapProducto(rs));
            }
        }
        return productos;
    }

    /**
     * Actualiza un producto existente en la tabla 'productos'.
     * @param producto El objeto Producto con los datos a actualizar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void actualizarProducto(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, ingredientes = ?, valor_neto = ?, valor_venta = ?, impuesto = ?, categoria_id = ? WHERE producto_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getIngredientes());
            stmt.setDouble(3, producto.getValorNeto());
            stmt.setDouble(4, producto.getValorVenta());
            stmt.setDouble(5, producto.getImpuesto());
            stmt.setInt(6, producto.getCategoriaId()); // Manejo del categoria_id
            stmt.setInt(7, producto.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina un producto de la tabla 'productos' por su ID.
     * @param id El ID del producto a eliminar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void eliminarProducto(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE producto_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Mapea un ResultSet a un objeto Producto.
     * @param rs El ResultSet que contiene los datos del producto.
     * @return Un objeto Producto con los datos del ResultSet.
     * @throws SQLException Si ocurre un error al acceder a los datos del ResultSet.
     */
    private Producto mapProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("producto_id"));
        p.setNombre(rs.getString("nombre"));
        p.setIngredientes(rs.getString("ingredientes"));
        p.setValorNeto(rs.getDouble("valor_neto"));
        p.setValorVenta(rs.getDouble("valor_venta"));
        p.setImpuesto(rs.getDouble("impuesto"));
        p.setCategoriaId(rs.getInt("categoria_id")); // Obtener categoria_id del ResultSet
        return p;
    }
}
