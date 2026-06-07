package com.letraaletra.api.features.user.domain.factory;

import com.letraaletra.api.features.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    private final UserFactory factory = new UserFactory();

    @Test
    @DisplayName("Deve fabricar um Usuário Local com propriedades iniciais corretas")
    void shouldCreateLocalUserWithDefaults() {
        User user = factory.createLocal("local-id", "NickLocal", "local@email.com", "password-hash");

        assertNotNull(user);
        assertEquals("local-id", user.getId());
        assertEquals("NickLocal", user.getNickname());
        assertEquals("local@email.com", user.getEmail());
        assertEquals("password-hash", user.getHashPassword());
        assertNull(user.getGoogleId(), "Usuários locais não devem possuir googleId");
        assertTrue(user.canChangeNickname());
        assertTrue(user.isNotInGame());
        assertTrue(user.getInventory().isEmpty());

        assertNotNull(user.getStats());
        assertEquals(0, user.getStats().getPoints());
        assertEquals(0, user.getStats().getWinStreak());
    }

    @Test
    @DisplayName("Deve fabricar um Usuário via Google sem nickname e sem hash de senha inicial")
    void shouldCreateGoogleUserWithDefaults() {
        User user = factory.createGoogle("google-id", "google@email.com", "sub-google-123");

        assertNotNull(user);
        assertEquals("google-id", user.getId());
        assertNull(user.getNickname(), "O nickname inicial de login social deve começar nulo para alteração posterior");
        assertEquals("google@email.com", user.getEmail());
        assertNull(user.getHashPassword(), "Cadastro via Google não possui hash de senha próprio");
        assertEquals("sub-google-123", user.getGoogleId());
        assertTrue(user.canChangeNickname());
        assertTrue(user.isNotInGame());

        assertNotNull(user.getStats());
        assertEquals(0, user.getStats().getTotalMatches());
    }
}