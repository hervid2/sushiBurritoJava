package com.restaurante.app.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Read-only encoder for the unsalted SHA-256 hashes written before BCrypt was adopted.
 *
 * <p>It exists solely so accounts created under the old scheme can still log in once, after which
 * {@link PasswordEncoderConfig} re-hashes them with BCrypt. It deliberately refuses to
 * {@link #encode(CharSequence)}: nothing may ever write a legacy hash again.
 */
class LegacySha256PasswordEncoder implements PasswordEncoder {

    /**
     * @throws UnsupportedOperationException always; new passwords must be hashed with BCrypt
     */
    @Override
    public String encode(CharSequence rawPassword) {
        throw new UnsupportedOperationException(
                "SHA-256 is read-only: new passwords must be hashed with BCrypt.");
    }

    /**
     * @param rawPassword     the plain-text password supplied at login
     * @param encodedPassword the stored legacy hash
     * @return {@code true} when the password matches the legacy hash
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return MessageDigest.isEqual(
                sha256(rawPassword.toString()).getBytes(),
                encodedPassword.getBytes());
    }

    private String sha256(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawPassword.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available in this JVM", e);
        }
    }
}
