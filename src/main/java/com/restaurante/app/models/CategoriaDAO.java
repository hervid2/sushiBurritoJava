package main.java.com.restaurante.app.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.restaurante.app.database.Conexion;

public class CategoriaDAO {

    private final Connection connection;

    public CategoriaDAO() throws SQLException {
        this.connection = Conexion.getConnection();
    }

    public List<String> obtenerNombresCategorias() throws SQLException {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT nombre FROM categorias ORDER BY nombre ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
        }
        return nombres;
    }

    public int obtenerIdPorNombre(String nombre) throws SQLException {
        String sql = "SELECT categoria_id FROM categorias WHERE nombre = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("categoria_id");
            }
        }
        throw new SQLException("Categoría no encontrada: " + nombre);
    }

    public String obtenerNombrePorId(int categoriaId) throws SQLException {
        String sql = "SELECT nombre FROM categorias WHERE categoria_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoriaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        }
        return "Desconocida";
    }

    public Map<Integer, String> obtenerMapaCategoriasIdNombre() throws SQLException {
        Map<Integer, String> idToNameMap = new HashMap<>();
        String sql = "SELECT categoria_id, nombre FROM categorias";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                idToNameMap.put(rs.getInt("categoria_id"), rs.getString("nombre"));
            }
        }
        return idToNameMap;
    }

    public Map<String, Integer> obtenerMapaCategoriasNombreId() throws SQLException {
        Map<String, Integer> nameToIdMap = new HashMap<>();
        String sql = "SELECT categoria_id, nombre FROM categorias";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                nameToIdMap.put(rs.getString("nombre"), rs.getInt("categoria_id"));
            }
        }
        return nameToIdMap;
    }
}