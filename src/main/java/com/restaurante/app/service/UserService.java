package com.restaurante.app.service;

import com.restaurante.app.exception.EmailAlreadyInUseException;
import com.restaurante.app.exception.InvalidCredentialsException;
import com.restaurante.app.exception.UserNotFoundException;
import com.restaurante.app.exception.ValidationException;
import com.restaurante.app.models.User;
import com.restaurante.app.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Business rules for user accounts: authentication, registration, password reset and maintenance.
 *
 * <p>This service is deliberately free of any Swing dependency: it returns values or throws
 * {@link com.restaurante.app.exception.DomainException domain exceptions}, and lets the views decide
 * how to present the outcome. Persistence goes through {@link UserRepository}.
 */
@Service
public class UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w-\\.\\+]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} ]{6,40}$");

    private final UserRepository userRepository;

    /**
     * @param userRepository user repository injected by Spring
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user against the stored credentials.
     *
     * @param email    the account email
     * @param password the plain-text password to verify
     * @return the authenticated user
     * @throws ValidationException         if either field is blank
     * @throws UserNotFoundException       if no account exists for the email
     * @throws InvalidCredentialsException if the password does not match
     */
    public User authenticate(String email, String password)
            throws ValidationException, UserNotFoundException, InvalidCredentialsException {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new ValidationException(List.of("Correo y contraseña no pueden estar vacíos."));
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Correo no registrado."));
        if (!user.getPassword().equals(hashPassword(password))) {
            throw new InvalidCredentialsException("Contraseña incorrecta.");
        }
        return user;
    }

    /**
     * @param email the email to check
     * @return {@code true} if an account exists for the given email
     */
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Resets a user's password.
     *
     * @param email       the account email
     * @param newPassword the new plain-text password
     * @throws ValidationException   if the new password is blank
     * @throws UserNotFoundException if no account exists for the email
     */
    @Transactional
    public void resetPassword(String email, String newPassword)
            throws ValidationException, UserNotFoundException {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ValidationException(List.of("La nueva contraseña no puede estar vacía."));
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));
        user.setPassword(hashPassword(newPassword));
        userRepository.save(user);
    }

    /**
     * Registers a new user after validating the supplied data.
     *
     * @param name     the display name
     * @param email    the email
     * @param role     the role (administrador / cocinero / mesero)
     * @param password the plain-text password
     * @throws ValidationException if any field breaks a validation rule
     */
    @Transactional
    public void registerUser(String name, String email, String role, String password)
            throws ValidationException {
        List<String> errors = validateCredentials(name, email, role, password);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(role.toLowerCase());
        user.setPassword(hashPassword(password));
        userRepository.save(user);
    }

    /**
     * Validates the data required to create a user without persisting anything.
     *
     * @return the list of validation errors, empty when the data is valid
     */
    public List<String> validateCredentials(String name, String email, String role, String password) {
        List<String> errors = new ArrayList<>();

        if (name == null || !NAME_PATTERN.matcher(name).matches()) {
            errors.add("El nombre debe tener entre 6 y 40 letras y puede incluir espacios.");
        } else if (userRepository.findByNameIgnoreCase(name).isPresent()) {
            errors.add("El nombre de usuario ya está en uso.");
        }

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("El correo electrónico no tiene un formato válido.");
        } else if (userRepository.findByEmail(email).isPresent()) {
            errors.add("Ya existe una cuenta con ese correo.");
        }

        if (role == null || !(role.equalsIgnoreCase("administrador")
                || role.equalsIgnoreCase("cocinero") || role.equalsIgnoreCase("mesero"))) {
            errors.add("El rol debe ser 'administrador', 'cocinero' o 'mesero'.");
        }

        if (password == null || password.length() < 8
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*[0-9].*")
                || !password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            errors.add("La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y un carácter especial.");
        } else if (password.equals(name)) {
            errors.add("La contraseña no puede ser igual al nombre de usuario.");
        }

        return errors;
    }

    /**
     * Deletes a user identified by email.
     *
     * @param email the account email
     * @throws ValidationException   if the email is blank
     * @throws UserNotFoundException if no account exists for the email
     */
    @Transactional
    public void deleteUser(String email) throws ValidationException, UserNotFoundException {
        if (email == null || email.isEmpty()) {
            throw new ValidationException(List.of("El correo para eliminar no puede estar vacío."));
        }
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new UserNotFoundException("No se encontró un usuario con ese correo para eliminar.");
        }
        userRepository.deleteByEmail(email);
    }

    /**
     * Changes a user's email address, ensuring the new one is not already taken.
     *
     * @param oldEmail the current email
     * @param newEmail the new email
     * @throws ValidationException        if either field is blank or both are equal
     * @throws UserNotFoundException      if no account exists for the current email
     * @throws EmailAlreadyInUseException if the new email belongs to another user
     */
    @Transactional
    public void updateUserEmail(String oldEmail, String newEmail)
            throws ValidationException, UserNotFoundException, EmailAlreadyInUseException {
        if (oldEmail == null || oldEmail.isEmpty() || newEmail == null || newEmail.isEmpty()) {
            throw new ValidationException(List.of("Los campos de correo no pueden estar vacíos."));
        }
        if (oldEmail.equals(newEmail)) {
            throw new ValidationException(List.of("El nuevo correo no puede ser igual al actual."));
        }

        User existing = userRepository.findByEmail(oldEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "No se encontró un usuario con el correo actual: " + oldEmail));

        User withNewEmail = userRepository.findByEmail(newEmail).orElse(null);
        if (withNewEmail != null && !withNewEmail.getId().equals(existing.getId())) {
            throw new EmailAlreadyInUseException(
                    "El nuevo correo '" + newEmail + "' ya está en uso por otro usuario.");
        }

        existing.setEmail(newEmail);
        userRepository.save(existing);
    }

    /**
     * Hashes a password with SHA-256. Replaced by BCrypt in a later iteration.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
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
