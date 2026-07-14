/**
 * Application service layer holding the business rules of the domain.
 *
 * <p>Services in this package are free of any presentation concern (no Swing): they return values or
 * raise {@link com.restaurante.app.exception.DomainException domain exceptions}, leaving the views to
 * decide how to inform the user. Persistence is delegated to Spring Data JPA repositories, and write
 * operations are wrapped in {@link org.springframework.transaction.annotation.Transactional} at this
 * layer.
 */
package com.restaurante.app.service;
