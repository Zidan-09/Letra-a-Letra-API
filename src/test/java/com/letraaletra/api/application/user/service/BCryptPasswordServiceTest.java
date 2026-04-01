package com.letraaletra.api.application.user.service;

import com.letraaletra.api.infrastructure.security.BCryptPasswordService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BCryptPasswordServiceTest {

    @InjectMocks
    private BCryptPasswordService BCryptPasswordService;

    @Test
    @DisplayName("Should hash the password")
    void hash() {
        String raw = "12345";

        String hash = BCryptPasswordService.hash(raw);

        Assertions.assertNotNull(hash);
        Assertions.assertNotEquals(raw, hash);
    }

    @Test
    @DisplayName("Should match the passwords")
    void matches() {
        String raw = "12345";
        String hash = BCryptPasswordService.hash(raw);

        boolean matches = BCryptPasswordService.matches(raw, hash);

        Assertions.assertTrue(matches);
    }

    @Test
    @DisplayName("Should not match")
    void notMatch() {
        String raw = "12345";
        String wrong = "54321";
        String hash = BCryptPasswordService.hash(raw);

        boolean matches = BCryptPasswordService.matches(wrong, hash);

        Assertions.assertFalse(matches);
    }
}