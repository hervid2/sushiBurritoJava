package com.restaurante.app.repository;

import com.restaurante.app.models.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for {@link User} accounts.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * @param email the account email
     * @return the matching user, if any
     */
    Optional<User> findByEmail(String email);

    /**
     * @param name the display name (case-insensitive)
     * @return the matching user, if any
     */
    Optional<User> findByNameIgnoreCase(String name);

    /**
     * Deletes the account identified by its email.
     *
     * @param email the account email
     */
    void deleteByEmail(String email);
}
