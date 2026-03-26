package com.letraaletra.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private PasswordService passwordService;

    @Test
    @DisplayName("Should hash the password")
    void hash() {
        String raw = "12345";

        String hash = passwordService.hash(raw);

        Assertions.assertNotNull(hash);
        Assertions.assertNotEquals(raw, hash);
    }

    @Test
    @DisplayName("Should match the passwords")
    void matches() {
        String raw = "12345";
        String hash = passwordService.hash(raw);

        boolean matches = passwordService.matches(raw, hash);

        Assertions.assertTrue(matches);
    }

    @Test
    @DisplayName("Should not match")
    void notMatch() {
        String raw = "12345";
        String wrong = "54321";
        String hash = passwordService.hash(raw);

        boolean matches = passwordService.matches(wrong, hash);

        Assertions.assertFalse(matches);
    }
}