package main.java.com.restaurante.app.controllers;

import main.java.com.restaurante.app.database.ProductoDAO;
import main.java.com.restaurante.app.models.CategoriaDAO;
import main.java.com.restaurante.app.models.Producto;

import java.sql.SQLException;
import java.util.List;
import java.util.Map; // Importar Map para los nuevos métodos de categoría

public class ProductoController {

    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;

    /**
     * Constructor de ProductoController.
     * Inicializa las DAOs para productos y categorías.
     * @throws SQLException Si ocurre un error al conectar con la base de datos.
     */
    public ProductoController() throws SQLException {
        this.productoDAO = new ProductoDAO();
        this.categoriaDAO = new CategoriaDAO();
    }

    /**
     * Inserta un nuevo producto en la base de datos.
     * @param producto El objeto Producto a insertar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void insertar(Producto producto) throws SQLException {
        productoDAO.insertarProducto(producto);
    }

    /**
     * Actualiza un producto existente en la base de datos.
     * @param producto El objeto Producto con los datos actualizados.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void actualizar(Producto producto) throws SQLException {
        productoDAO.actualizarProducto(producto);
    }

    /**
     * Elimina un producto de la base de datos por su ID.
     * @param productoId El ID del producto a eliminar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public void eliminar(int productoId) throws SQLException {
        productoDAO.eliminarProducto(productoId);
    }

    /**
     * Obtiene una lista de todos los productos de la base de datos.
     * @return Una lista de objetos Producto.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<Producto> obtenerTodos() throws SQLException {
        return productoDAO.obtenerTodosLosProductos();
    }

    /**
     * Obtiene los nombres de todas las categorías.
     * Útil para poblar JComboBox.
     * @return Un array de Strings con los nombres de las categorías.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public String[] obtenerCategorias() throws SQLException {
        return categoriaDAO.obtenerNombresCategorias().toArray(new String[0]);
    }

    /**
     * Obtiene el ID de una categoría dado su nombre.
     * Útil para guardar el ID de la categoría seleccionada por el usuario.
     * @param nombreCategoria El nombre de la categoría.
     * @return El ID de la categoría.
     * @throws SQLException Si la categoría no se encuentra o hay un error de SQL.
     */
    public int obtenerIdPorNombre(String nombreCategoria) throws SQLException {
        return categoriaDAO.obtenerIdPorNombre(nombreCategoria);
    }

    /**
     * Nuevo método: Obtiene el nombre de una categoría dado su ID.
     * Útil para mostrar el nombre de la categoría en la tabla a partir del ID guardado en el producto.
     * @param categoriaId El ID de la categoría.
     * @return El nombre de la categoría o "Desconocida" si no se encuentra.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public String obtenerNombreCategoriaPorId(int categoriaId) throws SQLException {
        return categoriaDAO.obtenerNombrePorId(categoriaId);
    }

    /**
     * Nuevo método: Obtiene un mapa de IDs de categorías a nombres de categorías.
     * Esto carga todas las categorías una vez para evitar múltiples llamadas a la BD.
     * @return Un mapa donde la clave es el ID de la categoría y el valor es el nombre.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public Map<Integer, String> obtenerMapaCategoriasIdNombre() throws SQLException {
        return categoriaDAO.obtenerMapaCategoriasIdNombre();
    }

    /**
     * Nuevo método: Obtiene un mapa de nombres de categorías a IDs de categorías.
     * Esto carga todas las categorías una vez para evitar múltiples llamadas a la BD.
     * @return Un mapa donde la clave es el nombre de la categoría y el valor es el ID.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public Map<String, Integer> obtenerMapaCategoriasNombreId() throws SQLException {
        return categoriaDAO.obtenerMapaCategoriasNombreId();
    }
}