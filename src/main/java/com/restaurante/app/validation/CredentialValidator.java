package com.restaurante.app.validation;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Single source of truth for the format rules applied to user credentials.
 *
 * <p>Centralising them here keeps every entry point (registration, password reset) enforcing the
 * same rules, and keeps the rule set readable as a list of predicate/message pairs rather than a
 * chain of {@code if} statements. Uniqueness checks are not rules of this class: they need the
 * database and therefore live in {@link com.restaurante.app.service.UserService}.
 *
 * <p>Messages are user-facing and stay in Spanish, matching the Swing UI.
 */
@Component
public class CredentialValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w-\\.\\+]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} ]{6,40}$");

    private static final List<String> ROLES = List.of("administrador", "cocinero", "mesero");

    private static final List<Rule<String>> NAME_RULES = List.of(
            new Rule<>(name -> NAME_PATTERN.matcher(name).matches(),
                    "El nombre debe tener entre 6 y 40 letras y puede incluir espacios."));

    private static final List<Rule<String>> EMAIL_RULES = List.of(
            new Rule<>(email -> EMAIL_PATTERN.matcher(email).matches(),
                    "El correo electrónico no tiene un formato válido."));

    private static final List<Rule<String>> ROLE_RULES = List.of(
            new Rule<>(role -> ROLES.stream().anyMatch(role::equalsIgnoreCase),
                    "El rol debe ser 'administrador', 'cocinero' o 'mesero'."));

    // Reported as one message so the user sees the whole password policy at once, as before.
    private static final List<Rule<String>> PASSWORD_RULES = List.of(
            new Rule<>(password -> password.length() >= 8
                    && password.matches(".*[A-Z].*")
                    && password.matches(".*[a-z].*")
                    && password.matches(".*[0-9].*")
                    && password.matches(".*[!@#$%^&*(),.?\":{}|<>].*"),
                    "La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, "
                            + "minúsculas, números y un carácter especial."));

    /**
     * @param name the display name to check
     * @return the validation errors, empty when the name is valid
     */
    public List<String> validateName(String name) {
        return apply(NAME_RULES, name, "El nombre no puede estar vacío.");
    }

    /**
     * @param email the email to check
     * @return the validation errors, empty when the email is valid
     */
    public List<String> validateEmail(String email) {
        return apply(EMAIL_RULES, email, "El correo no puede estar vacío.");
    }

    /**
     * @param role the role to check
     * @return the validation errors, empty when the role is valid
     */
    public List<String> validateRole(String role) {
        return apply(ROLE_RULES, role, "El rol no puede estar vacío.");
    }

    /**
     * Checks the password strength policy.
     *
     * @param password the plain-text password to check
     * @return the validation errors, empty when the password is strong enough
     */
    public List<String> validatePassword(String password) {
        return apply(PASSWORD_RULES, password, "La contraseña no puede estar vacía.");
    }

    private List<String> apply(List<Rule<String>> rules, String value, String blankMessage) {
        if (value == null || value.isBlank()) {
            return List.of(blankMessage);
        }
        List<String> errors = new ArrayList<>();
        for (Rule<String> rule : rules) {
            if (!rule.isSatisfiedBy(value)) {
                errors.add(rule.message());
            }
        }
        return errors;
    }

    /**
     * A single validation rule: the condition a valid value satisfies and the message shown when it
     * does not.
     *
     * @param condition predicate a valid value must satisfy
     * @param message   user-facing error reported when the condition fails
     * @param <T>       the validated type
     */
    private record Rule<T>(Predicate<T> condition, String message) {

        boolean isSatisfiedBy(T value) {
            return condition.test(value);
        }
    }
}
