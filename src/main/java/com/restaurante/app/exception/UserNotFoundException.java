package com.restaurante.app.exception;

/**
 * Raised when a user cannot be located for a given lookup key (email or username).
 */
public class UserNotFoundException extends DomainException {

    /**
     * @param message description of the missing user
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
