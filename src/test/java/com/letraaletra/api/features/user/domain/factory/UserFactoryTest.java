package com.letraaletra.api.features.user.domain.factory;

import com.letraaletra.api.features.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {
    @Test
    @DisplayName("Deve fabricar um Usuário Local com propriedades iniciais corretas")
    void shouldCreateLocalUserWithDefaults() {
        User user = UserFactory.createLocal("NickLocal", "local@email.com", "password-hash");

        assertNotNull(user);
        assertEquals("NickLocal", user.getUsername());
        assertEquals("local@email.com", user.getEmail());
        assertEquals("password-hash", user.getPasswordHash());
        assertNull(user.getGoogleId(), "Usuários locais não devem possuir googleId");
        assertTrue(user.canChangeNickname());
        assertTrue(user.isNotInGame());

        assertNotNull(user.getStats());
        assertEquals(0, user.getStats().getRankingPoints());
        assertEquals(0, user.getStats().getWinStreak());
    }

    @Test
    @DisplayName("Deve fabricar um Usuário via Google sem hash de senha inicial")
    void shouldCreateGoogleUserWithDefaults() {
        User user = UserFactory.createGoogle("NickLocal", "google@email.com", "sub-google-123");

        assertNotNull(user);
        assertEquals("google@email.com", user.getEmail());
        assertNull(user.getPasswordHash(), "Cadastro via Google não possui hash de senha próprio");
        assertEquals("sub-google-123", user.getGoogleId());
        assertTrue(user.canChangeNickname());
        assertTrue(user.isNotInGame());

        assertNotNull(user.getStats());
        assertEquals(0, user.getStats().getTotalMatches());
    }
}