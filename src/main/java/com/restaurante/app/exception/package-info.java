/**
 * Domain exceptions raised by the service layer to signal broken business rules.
 *
 * <p>Using dedicated exceptions keeps the services decoupled from the UI: instead of showing dialogs
 * or returning {@code null}, they throw a {@link com.restaurante.app.exception.DomainException} that
 * the views translate into the appropriate message.
 */
package com.restaurante.app.exception;
