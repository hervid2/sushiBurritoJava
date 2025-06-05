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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UsuarioController {
    private final UsuarioDAO usuarioDAO;

    public UsuarioController() throws SQLException {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Verifica si un usuario existe en la base de datos por su correo electrónico.
     * @param correo El correo electrónico a verificar.
     * @return true si el usuario existe, false en caso contrario.
     * @throws SQLException Si ocurre un error de SQL al consultar la base de datos.
     */
    public boolean usuarioExistePorCorreo(String correo) throws SQLException {
        return usuarioDAO.obtenerPorCorreo(correo) != null;
    }

    /**
     * Reestablece la contraseña de un usuario dado su correo electrónico y la nueva contraseña.
     * @param correo El correo electrónico del usuario.
     * @param nuevaContrasena La nueva contraseña.
     * @return true si la contraseña se reestablece exitosamente, false en caso contrario.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public boolean reestablecerContrasena(String correo, String nuevaContrasena) throws SQLException {
        if (nuevaContrasena == null || nuevaContrasena.isEmpty()) {
            JOptionPane.showMessageDialog(null, "La nueva contraseña no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Usuario usuario = usuarioDAO.obtenerPorCorreo(correo);
        if (usuario == null) {
            JOptionPane.showMessageDialog(null, "Usuario no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String hashNuevaContrasena = hashPassword(nuevaContrasena);
        usuario.setContrasena(hashNuevaContrasena);
        return usuarioDAO.actualizar(usuario); // Ajustado para que UsuarioDAO.actualizar devuelva boolean
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
            	new WaiterPanelView(usuario.getId()).setVisible(true); // Usar getId() según tu Usuario.java
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

    public List<String> validarCredenciales(String nombre, String correo, String rol, String contrasena) throws SQLException {
        List<String> errores = new ArrayList<>();

        if (nombre == null || !nombre.matches("^[\\p{L} ]{6,40}$")) {
            errores.add("El nombre debe tener entre 6 y 40 letras y puede incluir espacios.");
        } else if (usuarioDAO.obtenerPorNombre(nombre) != null) {
            errores.add("El nombre de usuario ya está en uso.");
        }

        if (correo == null || !Pattern.matches("^[\\w-\\.\\+]+@([\\w-]+\\.)+[\\w-]{2,4}$", correo)) {
            errores.add("El correo electrónico no tiene un formato válido.");
        } else if (usuarioDAO.obtenerPorCorreo(correo) != null) {
            errores.add("Ya existe una cuenta con ese correo.");
        }

        if (rol == null || !(rol.equalsIgnoreCase("administrador") || rol.equalsIgnoreCase("cocinero") || rol.equalsIgnoreCase("mesero"))) {
            errores.add("El rol debe ser 'administrador', 'cocinero' o 'mesero'.");
        }

        if (contrasena == null || contrasena.length() < 8 ||
                !contrasena.matches(".*[A-Z].*") ||
                !contrasena.matches(".*[a-z].*") ||
                !contrasena.matches(".*[0-9].*") ||
                !contrasena.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            errores.add("La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y un carácter especial.");
        } else if (contrasena.equals(nombre)) {
            errores.add("La contraseña no puede ser igual al nombre de usuario.");
        }

        return errores;
    }

    public boolean registrarUsuario(String nombre, String correo, String rol, String contrasena) throws SQLException {
        List<String> errores = validarCredenciales(nombre, correo, rol, contrasena);
        if (!errores.isEmpty()) {
            JOptionPane.showMessageDialog(null, String.join("\n", errores), "Errores de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String hash = hashPassword(contrasena);
        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setCorreo(correo);
        nuevo.setRol(rol.toLowerCase());
        nuevo.setContrasena(hash);
        return usuarioDAO.insertar(nuevo); // Ajustado para que UsuarioDAO.insertar devuelva boolean
    }

    // --- NUEVO MÉTODO PARA ELIMINAR USUARIO ---
    /**
     * Elimina un usuario de la base de datos por su correo electrónico.
     * @param correo El correo electrónico del usuario a eliminar.
     * @return true si el usuario fue eliminado exitosamente, false si no se encontró o hubo un error.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public boolean eliminarUsuario(String correo) throws SQLException {
        if (correo == null || correo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El correo para eliminar no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Verificar que el usuario existe antes de intentar eliminar.
        Usuario usuarioAEliminar = usuarioDAO.obtenerPorCorreo(correo);
        if (usuarioAEliminar == null) {
            JOptionPane.showMessageDialog(null, "No se encontró un usuario con ese correo para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Llamar al método eliminar del DAO que ahora recibe un correo
        return usuarioDAO.eliminar(correo); 
    }

    // --- NUEVO MÉTODO PARA ACTUALIZAR CORREO DE USUARIO ---
    /**
     * Actualiza el correo electrónico de un usuario.
     * @param oldCorreo El correo electrónico actual del usuario.
     * @param newCorreo El nuevo correo electrónico para el usuario.
     * @return true si el correo fue actualizado exitosamente, false si el usuario no existe o el nuevo correo ya está en uso.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public boolean actualizarCorreoUsuario(String oldCorreo, String newCorreo) throws SQLException {
        if (oldCorreo == null || oldCorreo.isEmpty() || newCorreo == null || newCorreo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Los campos de correo no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (oldCorreo.equals(newCorreo)) {
            JOptionPane.showMessageDialog(null, "El nuevo correo no puede ser igual al actual.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 1. Verificar si el usuario con el correo antiguo existe
        Usuario usuarioExistente = usuarioDAO.obtenerPorCorreo(oldCorreo);
        if (usuarioExistente == null) {
            JOptionPane.showMessageDialog(null, "No se encontró un usuario con el correo actual: " + oldCorreo, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // 2. Verificar si el nuevo correo ya está en uso por OTRA persona
        Usuario usuarioConNuevoCorreo = usuarioDAO.obtenerPorCorreo(newCorreo);
        // Si se encontró un usuario con el nuevo correo Y su ID es diferente al del usuario que estamos actualizando
        if (usuarioConNuevoCorreo != null && usuarioConNuevoCorreo.getId() != usuarioExistente.getId()) {
            JOptionPane.showMessageDialog(null, "El nuevo correo '" + newCorreo + "' ya está en uso por otro usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Si todo es válido, procede a actualizar llamando al nuevo método en DAO
        return usuarioDAO.actualizarCorreo(oldCorreo, newCorreo);
    }

    // Tu método hashPassword existente
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
            // Es mejor relanzar como una RuntimeException si es un error irrecuperable
            // o manejarlo de forma más específica si es posible.
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }
}