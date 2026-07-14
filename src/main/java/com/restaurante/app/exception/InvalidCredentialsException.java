package com.restaurante.app.exception;

/**
 * Raised during authentication when the supplied password does not match the stored one.
 */
public class InvalidCredentialsException extends DomainException {

    /**
     * @param message description of the authentication failure
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
