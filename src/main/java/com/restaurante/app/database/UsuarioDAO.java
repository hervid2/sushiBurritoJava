package main.java.com.restaurante.app.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.java.com.restaurante.app.models.Usuario;

public class UsuarioDAO {

    private final Connection connection;

    public UsuarioDAO() throws SQLException {
        this.connection = Conexion.getConnection();
    }

    public void insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, rol, correo, contrasena) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getRol());
            stmt.setString(3, usuario.getCorreo());
            stmt.setString(4, usuario.getContrasena());
            stmt.executeUpdate();
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

    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, rol = ?, correo = ?, contrasena = ? WHERE usuario_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getRol());
            stmt.setString(3, usuario.getCorreo());
            stmt.setString(4, usuario.getContrasena());
            stmt.setInt(5, usuario.getId());
            stmt.executeUpdate();
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE usuario_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("usuario_id"));
        u.setNombre(rs.getString("nombre"));
        u.setRol(rs.getString("rol"));
        u.setCorreo(rs.getString("correo"));
        u.setContrasena(rs.getString("contrasena"));
        return u;
    }
} 
