package com.restaurante.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.restaurante.app.exception.InvalidCredentialsException;
import com.restaurante.app.exception.UserNotFoundException;
import com.restaurante.app.models.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Exercises the registration → authentication flow against a real (in-memory) database, checking that
 * a registered account can log in and that bad credentials are rejected with the right domain
 * exceptions. Each test runs in a transaction that is rolled back afterwards, keeping them isolated.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    private static final String NAME = "Integration Tester";
    private static final String EMAIL = "tester@sushiburrito.com";
    private static final String ROLE = "mesero";
    private static final String PASSWORD = "Passw0rd!";

    @Autowired
    private UserService userService;

    @Test
    void authenticate_afterRegistration_returnsUserAndDoesNotStorePlainTextPassword() throws Exception {
        userService.registerUser(NAME, EMAIL, ROLE, PASSWORD);

        User authenticated = userService.authenticate(EMAIL, PASSWORD);

        assertThat(authenticated.getEmail()).isEqualTo(EMAIL);
        assertThat(authenticated.getRole()).isEqualTo(ROLE);
        assertThat(authenticated.getPassword())
                .as("password must be stored hashed, never in plain text")
                .isNotEqualTo(PASSWORD)
                .startsWith("{bcrypt}");
    }

    @Test
    void authenticate_withWrongPassword_throwsInvalidCredentials() throws Exception {
        userService.registerUser(NAME, EMAIL, ROLE, PASSWORD);

        assertThatThrownBy(() -> userService.authenticate(EMAIL, "Wrong0rd!"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void authenticate_withUnknownEmail_throwsUserNotFound() {
        assertThatThrownBy(() -> userService.authenticate("nobody@sushiburrito.com", PASSWORD))
                .isInstanceOf(UserNotFoundException.class);
    }
}
