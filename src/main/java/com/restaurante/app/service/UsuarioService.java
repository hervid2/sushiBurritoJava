package com.restaurante.app.service;

import com.restaurante.app.database.UsuarioDAO;
import com.restaurante.app.exception.EmailAlreadyInUseException;
import com.restaurante.app.exception.InvalidCredentialsException;
import com.restaurante.app.exception.UserNotFoundException;
import com.restaurante.app.exception.ValidationException;
import com.restaurante.app.models.Usuario;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Business rules for user accounts: authentication, registration, password reset and maintenance.
 *
 * <p>This service is deliberately free of any Swing dependency: it returns values or throws
 * {@link com.restaurante.app.exception.DomainException domain exceptions}, and lets the views decide
 * how to present the outcome. It is prototype-scoped and holds a {@link UsuarioDAO} backed by a
 * dedicated JDBC connection; callers must invoke {@link #close()} when finished. This lifecycle is a
 * temporary bridge that disappears once persistence moves to Spring Data JPA.
 */
@Service
@Scope("prototype")
public class UsuarioService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w-\\.\\+]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} ]{6,40}$");

    private final UsuarioDAO usuarioDAO;

    /**
     * @param usuarioDAO user data-access object injected by Spring
     */
    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Releases the underlying JDBC connection. Must be called when the caller is done with the service.
     */
    public void close() {
        usuarioDAO.close();
    }

    /**
     * Authenticates a user against the stored credentials.
     *
     * @param correo     the account email
     * @param contrasena the plain-text password to verify
     * @return the authenticated user
     * @throws ValidationException         if either field is blank
     * @throws UserNotFoundException       if no account exists for the email
     * @throws InvalidCredentialsException if the password does not match
     * @throws SQLException                if the lookup fails
     */
    public Usuario authenticate(String correo, String contrasena)
            throws ValidationException, UserNotFoundException, InvalidCredentialsException, SQLException {
        if (correo == null || correo.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            throw new ValidationException(List.of("Correo y contraseña no pueden estar vacíos."));
        }
        Usuario usuario = usuarioDAO.obtenerPorCorreo(correo);
        if (usuario == null) {
            throw new UserNotFoundException("Correo no registrado.");
        }
        if (!usuario.getContrasena().equals(hashPassword(contrasena))) {
            throw new InvalidCredentialsException("Contraseña incorrecta.");
        }
        return usuario;
    }

    /**
     * @param correo the email to check
     * @return {@code true} if an account exists for the given email
     * @throws SQLException if the lookup fails
     */
    public boolean existsByEmail(String correo) throws SQLException {
        return usuarioDAO.obtenerPorCorreo(correo) != null;
    }

    /**
     * Resets a user's password.
     *
     * @param correo          the account email
     * @param nuevaContrasena the new plain-text password
     * @throws ValidationException   if the new password is blank
     * @throws UserNotFoundException if no account exists for the email
     * @throws SQLException          if the update fails
     */
    public void resetPassword(String correo, String nuevaContrasena)
            throws ValidationException, UserNotFoundException, SQLException {
        if (nuevaContrasena == null || nuevaContrasena.isEmpty()) {
            throw new ValidationException(List.of("La nueva contraseña no puede estar vacía."));
        }
        Usuario usuario = usuarioDAO.obtenerPorCorreo(correo);
        if (usuario == null) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }
        usuario.setContrasena(hashPassword(nuevaContrasena));
        usuarioDAO.actualizar(usuario);
    }

    /**
     * Registers a new user after validating the supplied data.
     *
     * @param nombre     the display name
     * @param correo     the email
     * @param rol        the role (administrador / cocinero / mesero)
     * @param contrasena the plain-text password
     * @throws ValidationException if any field breaks a validation rule
     * @throws SQLException        if the insert fails
     */
    public void registerUser(String nombre, String correo, String rol, String contrasena)
            throws ValidationException, SQLException {
        List<String> errores = validateCredentials(nombre, correo, rol, contrasena);
        if (!errores.isEmpty()) {
            throw new ValidationException(errores);
        }
        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setCorreo(correo);
        nuevo.setRol(rol.toLowerCase());
        nuevo.setContrasena(hashPassword(contrasena));
        usuarioDAO.insertar(nuevo);
    }

    /**
     * Validates the data required to create a user without persisting anything.
     *
     * @return the list of validation errors, empty when the data is valid
     * @throws SQLException if uniqueness checks against the database fail
     */
    public List<String> validateCredentials(String nombre, String correo, String rol, String contrasena)
            throws SQLException {
        List<String> errores = new ArrayList<>();

        if (nombre == null || !NAME_PATTERN.matcher(nombre).matches()) {
            errores.add("El nombre debe tener entre 6 y 40 letras y puede incluir espacios.");
        } else if (usuarioDAO.obtenerPorNombre(nombre) != null) {
            errores.add("El nombre de usuario ya está en uso.");
        }

        if (correo == null || !EMAIL_PATTERN.matcher(correo).matches()) {
            errores.add("El correo electrónico no tiene un formato válido.");
        } else if (usuarioDAO.obtenerPorCorreo(correo) != null) {
            errores.add("Ya existe una cuenta con ese correo.");
        }

        if (rol == null || !(rol.equalsIgnoreCase("administrador")
                || rol.equalsIgnoreCase("cocinero") || rol.equalsIgnoreCase("mesero"))) {
            errores.add("El rol debe ser 'administrador', 'cocinero' o 'mesero'.");
        }

        if (contrasena == null || contrasena.length() < 8
                || !contrasena.matches(".*[A-Z].*")
                || !contrasena.matches(".*[a-z].*")
                || !contrasena.matches(".*[0-9].*")
                || !contrasena.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            errores.add("La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y un carácter especial.");
        } else if (contrasena.equals(nombre)) {
            errores.add("La contraseña no puede ser igual al nombre de usuario.");
        }

        return errores;
    }

    /**
     * Deletes a user identified by email.
     *
     * @param correo the account email
     * @throws ValidationException   if the email is blank
     * @throws UserNotFoundException if no account exists for the email
     * @throws SQLException          if the delete fails
     */
    public void deleteUser(String correo)
            throws ValidationException, UserNotFoundException, SQLException {
        if (correo == null || correo.isEmpty()) {
            throw new ValidationException(List.of("El correo para eliminar no puede estar vacío."));
        }
        if (usuarioDAO.obtenerPorCorreo(correo) == null) {
            throw new UserNotFoundException("No se encontró un usuario con ese correo para eliminar.");
        }
        usuarioDAO.eliminar(correo);
    }

    /**
     * Changes a user's email address, ensuring the new one is not already taken.
     *
     * @param oldCorreo the current email
     * @param newCorreo the new email
     * @throws ValidationException        if either field is blank or both are equal
     * @throws UserNotFoundException      if no account exists for the current email
     * @throws EmailAlreadyInUseException if the new email belongs to another user
     * @throws SQLException               if the update fails
     */
    public void updateUserEmail(String oldCorreo, String newCorreo)
            throws ValidationException, UserNotFoundException, EmailAlreadyInUseException, SQLException {
        if (oldCorreo == null || oldCorreo.isEmpty() || newCorreo == null || newCorreo.isEmpty()) {
            throw new ValidationException(List.of("Los campos de correo no pueden estar vacíos."));
        }
        if (oldCorreo.equals(newCorreo)) {
            throw new ValidationException(List.of("El nuevo correo no puede ser igual al actual."));
        }

        Usuario existente = usuarioDAO.obtenerPorCorreo(oldCorreo);
        if (existente == null) {
            throw new UserNotFoundException("No se encontró un usuario con el correo actual: " + oldCorreo);
        }

        Usuario conNuevoCorreo = usuarioDAO.obtenerPorCorreo(newCorreo);
        if (conNuevoCorreo != null && conNuevoCorreo.getId() != existente.getId()) {
            throw new EmailAlreadyInUseException(
                    "El nuevo correo '" + newCorreo + "' ya está en uso por otro usuario.");
        }

        usuarioDAO.actualizarCorreo(oldCorreo, newCorreo);
    }

    /**
     * Hashes a password with SHA-256. Replaced by BCrypt in a later iteration.
     */
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
