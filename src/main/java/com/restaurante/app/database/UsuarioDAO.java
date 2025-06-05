package main.java.com.restaurante.app.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.java.com.restaurante.app.models.Usuario;

public class UsuarioDAO {

    private final Connection connection;

    public UsuarioDAO() throws SQLException {
        // Usar tu clase de conexión existente 'Conexion'
        this.connection = Conexion.getConnection();
    }

    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, rol, correo, contrasena) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getRol());
            stmt.setString(3, usuario.getCorreo());
            stmt.setString(4, usuario.getContrasena());
            // Devuelve true si se insertó al menos una fila
            return stmt.executeUpdate() > 0;
        }
    }

    public Usuario obtenerPorCorreo(String correo) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUsuario(rs);
            }
        }
        return null;
    }

    public Usuario obtenerPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE LOWER(nombre) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUsuario(rs);
            }
        }
        return null;
    }

    public List<Usuario> obtenerTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usuarios.add(mapUsuario(rs));
            }
        }
        return usuarios;
    }

    public boolean actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, rol = ?, correo = ?, contrasena = ? WHERE usuario_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getRol());
            stmt.setString(3, usuario.getCorreo());
            stmt.setString(4, usuario.getContrasena());
            stmt.setInt(5, usuario.getId()); // Asumo que getId() es el ID de usuario
            // Devuelve true si se actualizó al menos una fila
            return stmt.executeUpdate() > 0;
        }
    }

    // --- MODIFICADO: ELIMINAR POR CORREO EN LUGAR DE ID ---
    public boolean eliminar(String correo) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE correo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, correo);
            // Devuelve true si se eliminó al menos una fila
            return stmt.executeUpdate() > 0;
        }
    }

    // --- NUEVO MÉTODO PARA ACTUALIZAR CORREO ---
    public boolean actualizarCorreo(String oldCorreo, String newCorreo) throws SQLException {
        String sql = "UPDATE usuarios SET correo = ? WHERE correo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newCorreo);
            stmt.setString(2, oldCorreo);
            // Devuelve true si se actualizó al menos una fila
            return stmt.executeUpdate() > 0;
        }
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("usuario_id")); // Asegúrate de que tu clase Usuario tiene setId
        u.setNombre(rs.getString("nombre"));
        u.setRol(rs.getString("rol"));
        u.setCorreo(rs.getString("correo"));
        u.setContrasena(rs.getString("contrasena"));
        return u;
    }
}