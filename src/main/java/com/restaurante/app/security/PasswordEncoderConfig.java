package com.restaurante.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

/**
 * Defines the application's password hashing strategy.
 *
 * <p>Services depend on the {@link PasswordEncoder} abstraction, never on a concrete algorithm, so
 * the strategy can be swapped here without touching business code.
 */
@Configuration
public class PasswordEncoderConfig {

    /** Identifier BCrypt hashes are stored with, e.g. {@code {bcrypt}$2a$10$...}. */
    private static final String BCRYPT_ID = "bcrypt";

    /**
     * Hashes new passwords with BCrypt (salted, adaptive) while still being able to verify the
     * unsalted SHA-256 hashes stored by earlier versions.
     *
     * <p>Storing the algorithm as a prefix is what makes future migrations possible: a hash always
     * carries the scheme that produced it. Legacy hashes have no prefix, which is exactly how
     * {@link DelegatingPasswordEncoder#upgradeEncoding(String)} recognises them and reports that they
     * must be re-hashed — see
     * {@link com.restaurante.app.service.UserService#authenticate(String, String)}.
     *
     * @return the encoder used to hash and verify every password in the application
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = Map.of(BCRYPT_ID, new BCryptPasswordEncoder());
        DelegatingPasswordEncoder encoder = new DelegatingPasswordEncoder(BCRYPT_ID, encoders);
        encoder.setDefaultPasswordEncoderForMatches(new LegacySha256PasswordEncoder());
        return encoder;
    }
}
