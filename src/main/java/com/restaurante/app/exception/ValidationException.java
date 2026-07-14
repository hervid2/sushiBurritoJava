package com.restaurante.app.exception;

import java.util.Collections;
import java.util.List;

/**
 * Raised when user-supplied data fails one or more business validation rules.
 *
 * <p>Carries every individual error so the view can present them together. The exception message is
 * the errors joined by line breaks, ready to be shown as-is.
 */
public class ValidationException extends DomainException {

    private final List<String> errors;

    /**
     * @param errors the collected validation error messages (must not be empty)
     */
    public ValidationException(List<String> errors) {
        super(String.join("\n", errors));
        this.errors = List.copyOf(errors);
    }

    /**
     * @return an unmodifiable view of the individual validation errors
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
