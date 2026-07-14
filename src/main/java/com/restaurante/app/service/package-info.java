/**
 * Application service layer holding the business rules of the domain.
 *
 * <p>Services in this package are free of any presentation concern (no Swing): they return values or
 * raise {@link com.restaurante.app.exception.DomainException domain exceptions}, leaving the views to
 * decide how to inform the user. While persistence still relies on JDBC DAOs, services are
 * prototype-scoped and expose a {@code close()} method to release the underlying connection; this
 * bridge disappears once Spring Data JPA is introduced.
 */
package com.restaurante.app.service;
