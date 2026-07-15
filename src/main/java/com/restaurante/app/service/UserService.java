package com.restaurante.app.service;

import com.restaurante.app.exception.EmailAlreadyInUseException;
import com.restaurante.app.exception.InvalidCredentialsException;
import com.restaurante.app.exception.UserNotFoundException;
import com.restaurante.app.exception.ValidationException;
import com.restaurante.app.models.User;
import com.restaurante.app.repository.UserRepository;
import com.restaurante.app.validation.CredentialValidator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Business rules for user accounts: authentication, registration, password reset and maintenance.
 *
 * <p>This service is deliberately free of any Swing dependency: it returns values or throws
 * {@link com.restaurante.app.exception.DomainException domain exceptions}, and lets the views decide
 * how to present the outcome. Persistence goes through {@link UserRepository}.
 *
 * <p>Passwords are never stored or compared in plain text: hashing is delegated to the injected
 * {@link PasswordEncoder} (see {@link com.restaurante.app.security.PasswordEncoderConfig}).
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialValidator credentialValidator;

    /**
     * @param userRepository      user repository injected by Spring
     * @param passwordEncoder     hashing strategy used to store and verify passwords
     * @param credentialValidator shared credential format rules
     */
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CredentialValidator credentialValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.credentialValidator = credentialValidator;
    }

    /**
     * Authenticates a user against the stored credentials.
     *
     * <p>Accounts still holding a hash from the old SHA-256 scheme are transparently re-hashed with
     * BCrypt on their next successful login, so no password reset is imposed on existing users.
     *
     * @param email    the account email
     * @param password the plain-text password to verify
     * @return the authenticated user
     * @throws ValidationException         if either field is blank
     * @throws UserNotFoundException       if no account exists for the email
     * @throws InvalidCredentialsException if the password does not match
     */
    @Transactional
    public User authenticate(String email, String password)
            throws ValidationException, UserNotFoundException, InvalidCredentialsException {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new ValidationException(List.of("Correo y contraseña no pueden estar vacíos."));
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Correo no registrado."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Contraseña incorrecta.");
        }
        if (passwordEncoder.upgradeEncoding(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
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
     * Resets a user's password, applying the same strength policy as registration.
     *
     * @param email       the account email
     * @param newPassword the new plain-text password
     * @throws ValidationException   if the new password does not meet the strength policy
     * @throws UserNotFoundException if no account exists for the email
     */
    @Transactional
    public void resetPassword(String email, String newPassword)
            throws ValidationException, UserNotFoundException {
        List<String> errors = credentialValidator.validatePassword(newPassword);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));
        user.setPassword(passwordEncoder.encode(newPassword));
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
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    /**
     * Validates the data required to create a user without persisting anything.
     *
     * <p>Format rules come from {@link CredentialValidator}; this method adds the checks that need
     * the database (name and email uniqueness), which are only worth running once the value itself
     * is well-formed.
     *
     * @param name     the display name
     * @param email    the email
     * @param role     the role (administrador / cocinero / mesero)
     * @param password the plain-text password
     * @return the list of validation errors, empty when the data is valid
     */
    public List<String> validateCredentials(String name, String email, String role, String password) {
        List<String> errors = new ArrayList<>();

        List<String> nameErrors = credentialValidator.validateName(name);
        errors.addAll(nameErrors);
        if (nameErrors.isEmpty() && userRepository.findByNameIgnoreCase(name).isPresent()) {
            errors.add("El nombre de usuario ya está en uso.");
        }

        List<String> emailErrors = credentialValidator.validateEmail(email);
        errors.addAll(emailErrors);
        if (emailErrors.isEmpty() && userRepository.findByEmail(email).isPresent()) {
            errors.add("Ya existe una cuenta con ese correo.");
        }

        errors.addAll(credentialValidator.validateRole(role));

        List<String> passwordErrors = credentialValidator.validatePassword(password);
        errors.addAll(passwordErrors);
        if (passwordErrors.isEmpty() && password.equals(name)) {
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
}
