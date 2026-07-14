package com.restaurante.app.exception;

/**
 * Base type for business-rule failures raised by the service layer.
 *
 * <p>The service layer must stay free of any presentation concern, so instead of showing dialogs it
 * throws these exceptions. The Swing views catch them and decide how to inform the user.
 */
public class DomainException extends Exception {

    /**
     * @param message human-readable description of the broken business rule
     */
    public DomainException(String message) {
        super(message);
    }
}
