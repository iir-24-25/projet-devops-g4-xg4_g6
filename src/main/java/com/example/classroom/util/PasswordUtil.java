package com.example.classroom.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // bytes

    // Generates a random salt
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    // Hashes the password with the given salt
    public static String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt); // Add salt to the digest
            byte[] hashedPasswordBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPasswordBytes);
        } catch (NoSuchAlgorithmException e) {
            // This should ideally not happen with a standard algorithm like SHA-256
            throw new RuntimeException("Error hashing password: Algorithm not found", e);
        }
    }

    // Verifies a password against a stored hash and salt
    public static boolean verifyPassword(String plainPassword, String storedHashedPassword, byte[] salt) {
        String newHashedPassword = hashPassword(plainPassword, salt);
        return newHashedPassword.equals(storedHashedPassword);
    }
}