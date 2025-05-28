package main.java.com.restaurante.app.controllers;

import main.java.com.restaurante.app.database.UsuarioDAO;
import main.java.com.restaurante.app.models.Usuario;
import main.java.com.restaurante.app.views.admin.AdminPanelView;
import main.java.com.restaurante.app.views.cocina.CocinaPanelView;
import main.java.com.restaurante.app.views.mesero.WaiterPanelView;

import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;
    private String correoTemporal;

    public UsuarioController() throws SQLException {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean login(String correo, String contrasena, JFrame loginFrame) throws SQLException {
        if (correo == null || correo.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Correo y contraseña no pueden estar vacíos.");
            return false;
        }
        Usuario usuario = usuarioDAO.obtenerPorCorreo(correo);
        if (usuario == null) {
            JOptionPane.showMessageDialog(null, "Correo no registrado.");
            return false;
        }
        String hash = hashPassword(contrasena);
        if (!usuario.getContrasena().equals(hash)) {
            JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");
            return false;
        }

        loginFrame.dispose();
        switch (usuario.getRol().toLowerCase()) {
            case "administrador":
                new AdminPanelView().setVisible(true);
                break;
            case "mesero":
                new WaiterPanelView().setVisible(true);
                break;
            case "cocinero":
                new CocinaPanelView().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Rol de usuario no reconocido.");
                return false;
        }
        return true;
    }

    public boolean registrarUsuario(String nombre, String correo, String rol, String contrasena) throws SQLException {
        if (usuarioDAO.obtenerPorCorreo(correo) != null) return false;
        String hash = hashPassword(contrasena);
        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setCorreo(correo);
        nuevo.setRol(rol);
        nuevo.setContrasena(hash);
        usuarioDAO.insertar(nuevo);
        return true;
    }

    public boolean actualizarContrasena(String correo, String nuevaContrasena) throws SQLException {
        Usuario usuario = usuarioDAO.obtenerPorCorreo(correo);
        if (usuario == null) return false;
        usuario.setContrasena(hashPassword(nuevaContrasena));
        usuarioDAO.actualizar(usuario);
        return true;
    }

    public boolean usuarioExistePorCorreo(String correo) throws SQLException {
        return usuarioDAO.obtenerPorCorreo(correo) != null;
    }

    public List<Usuario> listarUsuarios() throws SQLException {
        return usuarioDAO.obtenerTodos();
    }

    public boolean eliminarUsuario(int id) throws SQLException {
        return usuarioDAO.eliminar(id);
    }

    public void setCorreoTemporal(String correo) {
        this.correoTemporal = correo;
    }

    public String getCorreoTemporal() {
        return correoTemporal;
    }

    private String hashPassword(String contrasena) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(contrasena.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }
} 
