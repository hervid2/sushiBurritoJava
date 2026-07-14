package com.restaurante.app.service;

import com.restaurante.app.database.CategoriaDAO;
import com.restaurante.app.database.ProductoDAO;
import com.restaurante.app.models.Producto;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Business rules for products and their categories, used by the menu-management screens.
 *
 * <p>Free of Swing dependencies. Prototype-scoped so it owns a dedicated JDBC connection through
 * {@link ProductoDAO}; callers must invoke {@link #close()} when finished.
 */
@Service
@Scope("prototype")
public class ProductoService {

    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;

    /**
     * @param productoDAO  product data-access object injected by Spring
     * @param categoriaDAO category data-access object injected by Spring
     */
    public ProductoService(ProductoDAO productoDAO, CategoriaDAO categoriaDAO) {
        this.productoDAO = productoDAO;
        this.categoriaDAO = categoriaDAO;
    }

    /**
     * Releases the underlying JDBC connection held by this service.
     */
    public void close() {
        productoDAO.close();
    }

    /**
     * @param producto the product to create
     * @throws SQLException if persistence fails
     */
    public void insertar(Producto producto) throws SQLException {
        productoDAO.insertarProducto(producto);
    }

    /**
     * @param producto the product carrying updated data
     * @throws SQLException if persistence fails
     */
    public void actualizar(Producto producto) throws SQLException {
        productoDAO.actualizarProducto(producto);
    }

    /**
     * @param productoId the id of the product to delete
     * @throws SQLException if persistence fails
     */
    public void eliminar(int productoId) throws SQLException {
        productoDAO.eliminarProducto(productoId);
    }

    /**
     * @return every product
     * @throws SQLException if the lookup fails
     */
    public List<Producto> obtenerTodos() throws SQLException {
        return productoDAO.obtenerTodosLosProductos();
    }

    /**
     * @return the category names, alphabetically ordered, for populating combo boxes
     * @throws SQLException if the lookup fails
     */
    public String[] obtenerCategorias() throws SQLException {
        return categoriaDAO.obtenerNombresCategorias().toArray(new String[0]);
    }

    /**
     * @param nombreCategoria the category name
     * @return the id of the category
     * @throws SQLException if the category is not found or the lookup fails
     */
    public int obtenerIdPorNombre(String nombreCategoria) throws SQLException {
        return categoriaDAO.obtenerIdPorNombre(nombreCategoria);
    }

    /**
     * @param categoriaId the category id
     * @return the category name, or a placeholder if not found
     * @throws SQLException if the lookup fails
     */
    public String obtenerNombreCategoriaPorId(int categoriaId) throws SQLException {
        return categoriaDAO.obtenerNombrePorId(categoriaId);
    }

    /**
     * @return a map of category id to category name, loaded in a single query
     * @throws SQLException if the lookup fails
     */
    public Map<Integer, String> obtenerMapaCategoriasIdNombre() throws SQLException {
        return categoriaDAO.obtenerMapaCategoriasIdNombre();
    }

    /**
     * @return a map of category name to category id, loaded in a single query
     * @throws SQLException if the lookup fails
     */
    public Map<String, Integer> obtenerMapaCategoriasNombreId() throws SQLException {
        return categoriaDAO.obtenerMapaCategoriasNombreId();
    }
}
