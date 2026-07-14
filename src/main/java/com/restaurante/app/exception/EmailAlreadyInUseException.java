package com.restaurante.app.exception;

/**
 * Raised when an email address is already registered to a different user.
 */
public class EmailAlreadyInUseException extends DomainException {

    /**
     * @param message description of the conflicting email
     */
    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
