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

    // ... (tus otros métodos existentes)

    /**
     * Verifica si un usuario existe en la base de datos por su correo electrónico.
     * @param correo El correo electrónico a verificar.
     * @return true si el usuario existe, false en caso contrario.
     * @throws SQLException Si ocurre un error de SQL al consultar la base de datos.
     */
    public boolean usuarioExistePorCorreo(String correo) throws SQLException {
        // Reutilizamos el método obtenerPorCorreo de UsuarioDAO
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
        // Aquí puedes agregar validaciones adicionales para la nueva contraseña si es necesario
        // antes de hashearla y actualizarla.
        if (nuevaContrasena == null || nuevaContrasena.isEmpty()) {
            return false; // O lanza una excepción o muestra un mensaje de error
        }

        // Primero, obtener el usuario para actualizar su contraseña
        Usuario usuario = usuarioDAO.obtenerPorCorreo(correo);
        if (usuario == null) {
            return false; // Usuario no encontrado
        }

        // Hashear la nueva contraseña
        String hashNuevaContrasena = hashPassword(nuevaContrasena);

        // Actualizar la contraseña en la base de datos
        // Necesitarás un método en UsuarioDAO para actualizar la contraseña de un usuario
        // por su ID o correo. Por ejemplo: usuarioDAO.actualizarContrasena(usuario.getId(), hashNuevaContrasena);
        // O si tu usuarioDAO.actualizar(Usuario) ya lo permite:
        usuario.setContrasena(hashNuevaContrasena);
        usuarioDAO.actualizar(usuario); // Asumiendo que 'actualizar' actualiza todos los campos incluyendo la contraseña
        return true;
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
            	new WaiterPanelView(usuario.getId()).setVisible(true);
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
        }
         else if (usuarioDAO.obtenerPorNombre(nombre) != null) {
            errores.add("El nombre de usuario ya está en uso.");
        }

        if (correo == null || !Pattern.matches("^[\\w-\\.+]+@([\\w-]+\\.)+[\\w-]{2,4}$", correo)) {
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
        usuarioDAO.insertar(nuevo);
        return true;
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

 
