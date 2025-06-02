package main.java.com.restaurante.app.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.java.com.restaurante.app.models.Producto;
import main.java.com.restaurante.app.models.Categoria; // Importar la clase Categoria

public class ProductoDAO {

    private final Connection connection;

    /**
     * Constructor de ProductoDAO.
     * Obtiene la conexión a la base de datos y la mantiene abierta para la vida de la instancia del DAO.
     * Es crucial llamar a close() cuando la instancia de ProductoDAO ya no se necesite.
     * @throws SQLException Si ocurre un error al obtener la conexión.
     */
    public ProductoDAO() throws SQLException {
        this.connection = Conexion.getConnection();
    }

    /**
     * Cierra la conexión a la base de datos.
     * Es importante llamar a este método cuando la instancia de ProductoDAO ya no se utilizará
     * para liberar los recursos de la base de datos.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión de ProductoDAO cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión de ProductoDAO: " + e.getMessage());
            e.printStackTrace();
        }
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
            stmt.setInt(6, producto.getCategoriaId());
            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene un producto por su ID.
     * @param productoId El ID del producto.
     * @return El objeto Producto si se encuentra, de lo contrario, null.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public Producto obtenerProductoPorId(int productoId) throws SQLException {
        String sql = "SELECT producto_id, nombre, ingredientes, valor_neto, valor_venta, impuesto, categoria_id FROM productos WHERE producto_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, productoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapProducto(rs); // ¡Usar el método de mapeo existente!
            }
        }
        return null;
    }
    
    public int obtenerIdPorNombre(String nombre) throws SQLException {
        String sql = "SELECT producto_id FROM productos WHERE nombre = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("producto_id");
        }
        throw new SQLException("Producto no encontrado: " + nombre);
    }

    /**
     * Obtiene una lista de todos los productos de la tabla 'productos'.
     * @return Una lista de objetos Producto.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Producto> obtenerTodosLosProductos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        // Ordena por categoría y luego por nombre para una mejor visualización en los JComboBox
        String sql = "SELECT p.producto_id, p.nombre, p.ingredientes, p.valor_neto, p.valor_venta, p.impuesto, p.categoria_id, c.nombre as categoria_nombre " +
                     "FROM productos p JOIN categorias c ON p.categoria_id = c.categoria_id " +
                     "ORDER BY c.nombre, p.nombre"; 

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                productos.add(mapProducto(rs));
            }
        }
        return productos;
    }

    /**
     * Obtiene una lista de todas las categorías de la tabla 'categorias'.
     * @return Una lista de objetos Categoria.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Categoria> obtenerTodasLasCategorias() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT categoria_id, nombre FROM categorias ORDER BY nombre";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setCategoriaId(rs.getInt("categoria_id"));
                categoria.setNombre(rs.getString("nombre"));
                categorias.add(categoria);
            }
        }
        return categorias;
    }

    public String obtenerNombrePorId(int productoId) throws SQLException {
        String sql = "SELECT nombre FROM productos WHERE producto_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("nombre");
        }
        return "Desconocido"; // Retorna "Desconocido" si no se encuentra
    }

    public String obtenerCategoriaPorProductoId(int productoId) throws SQLException {
        String sql = """
            SELECT c.nombre FROM productos p 
            JOIN categorias c ON p.categoria_id = c.categoria_id
            WHERE p.producto_id = ?
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("nombre");
        }
        return "Sin Categoría"; // Retorna "Sin Categoría" si no se encuentra o no tiene categoría
    }
    
    /**
     * Obtiene una categoría por su ID.
     * @param categoriaId El ID de la categoría.
     * @return El objeto Categoria si se encuentra, de lo contrario, null.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public Categoria obtenerCategoriaPorId(int categoriaId) throws SQLException {
        String sql = "SELECT categoria_id, nombre FROM categorias WHERE categoria_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoriaId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setCategoriaId(rs.getInt("categoria_id"));
                categoria.setNombre(rs.getString("nombre"));
                return categoria;
            }
        }
        return null;
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
            stmt.setInt(6, producto.getCategoriaId());
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
        p.setCategoriaId(rs.getInt("categoria_id"));
        // Si necesitas el valor_total del Producto, asegúrate de que esté en la tabla
        // y en el modelo Producto, y luego agrégalo aquí:
        // p.setValorTotal(rs.getDouble("valor_total"));
        return p;
    }
}
