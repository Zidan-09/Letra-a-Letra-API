package com.letraaletra.api.features.user.domain.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserSession Unit Tests")
class UserSessionTest {

    private UUID userId;
    private LocalDateTime now;
    private LocalDateTime expiresAt;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        now = LocalDateTime.now();
        expiresAt = now.plusDays(90);
    }

    @Nested
    @DisplayName("Criacao")
    class Creation {

        @Test
        @DisplayName("Deve criar sessao ativa sem historico de rotacao")
        void create_ShouldBuildActiveSessionWithoutRotationHistory() {
            UserSession session = UserSession.create(userId, "hash-a", now, expiresAt);

            assertNotNull(session.getId());
            assertEquals(userId, session.getUserId());
            assertEquals("hash-a", session.getCurrentTokenHash());
            assertNull(session.getPreviousTokenHash());
            assertNull(session.getPreviousTokenRotatedAt());
            assertFalse(session.isRevoked());
            assertFalse(session.isExpired(now));
            assertTrue(session.matchesCurrent("hash-a"));
            assertFalse(session.matchesPrevious("hash-a"));
        }
    }

    @Nested
    @DisplayName("Rotacao")
    class Rotation {

        @Test
        @DisplayName("Deve mover o token atual para anterior ao rotacionar")
        void rotate_ShouldMoveCurrentToPrevious() {
            UserSession session = UserSession.create(userId, "hash-a", now, expiresAt);
            LocalDateTime rotationTime = now.plusMinutes(1);
            LocalDateTime newExpiresAt = rotationTime.plusDays(90);

            session.rotate("hash-b", rotationTime, newExpiresAt);

            assertEquals("hash-b", session.getCurrentTokenHash());
            assertEquals("hash-a", session.getPreviousTokenHash());
            assertEquals(rotationTime, session.getPreviousTokenRotatedAt());
            assertEquals(newExpiresAt, session.getExpiresAt());
            assertTrue(session.matchesCurrent("hash-b"));
            assertTrue(session.matchesPrevious("hash-a"));
            assertFalse(session.matchesCurrent("hash-a"));
        }

        @Test
        @DisplayName("Deve descartar o anterior antigo apos segunda rotacao")
        void rotate_Twice_ShouldDiscardOldestToken() {
            UserSession session = UserSession.create(userId, "hash-a", now, expiresAt);
            session.rotate("hash-b", now.plusMinutes(1), expiresAt);
            session.rotate("hash-c", now.plusMinutes(2), expiresAt);

            assertEquals("hash-c", session.getCurrentTokenHash());
            assertEquals("hash-b", session.getPreviousTokenHash());
            assertFalse(session.matchesCurrent("hash-a"));
            assertFalse(session.matchesPrevious("hash-a"));
        }
    }

    @Nested
    @DisplayName("Janela de recuperacao")
    class RecoveryWindow {

        @Test
        @DisplayName("Deve aceitar o anterior dentro da janela")
        void isPreviousWithinRecoveryWindow_WhenInside_ShouldReturnTrue() {
            UserSession session = UserSession.create(userId, "hash-a", now, expiresAt);
            session.rotate("hash-b", now, expiresAt);

            assertTrue(session.isPreviousWithinRecoveryWindow(now.plusMinutes(4), 300_000L));
        }

        @Test
        @DisplayName("Deve rejeitar o anterior fora da janela")
        void isPreviousWithinRecoveryWindow_WhenOutside_ShouldReturnFalse() {
            UserSession session = UserSession.create(userId, "hash-a", now, expiresAt);
            session.rotate("hash-b", now, expiresAt);

            assertFalse(session.isPreviousWithinRecoveryWindow(now.plusMinutes(6), 300_000L));
        }

        @Test
        @DisplayName("Deve retornar falso quando nao houver rotacao anterior")
        void isPreviousWithinRecoveryWindow_WhenNeverRotated_ShouldReturnFalse() {
            UserSession session = UserSession.create(userId, "hash-a", now, expiresAt);

            assertFalse(session.isPreviousWithinRecoveryWindow(now, 300_000L));
        }
    }

    @Nested
    @DisplayName("Expiracao e revogacao")
    class ExpirationAndRevocation {

        @Test
        @DisplayName("Deve considerar expirada apos expires_at")
        void isExpired_WhenPastExpiresAt_ShouldReturnTrue() {
            UserSession session = UserSession.create(userId, "hash-a", now, now.plusMinutes(10));

            assertFalse(session.isExpired(now.plusMinutes(9)));
            assertTrue(session.isExpired(now.plusMinutes(10)));
            assertTrue(session.isExpired(now.plusMinutes(11)));
        }

        @Test
        @DisplayName("Deve revogar com motivo e ser idempotente")
        void revoke_ShouldSetRevocationDataAndBeIdempotent() {
            UserSession session = UserSession.create(userId, "hash-a", now, expiresAt);

            session.revoke(SessionRevocationReason.LOGOUT, now.plusMinutes(1));

            assertTrue(session.isRevoked());
            assertEquals(SessionRevocationReason.LOGOUT, session.getRevocationReason());
            assertEquals(now.plusMinutes(1), session.getRevokedAt());

            session.revoke(SessionRevocationReason.TOKEN_REUSE, now.plusMinutes(2));

            assertEquals(SessionRevocationReason.LOGOUT, session.getRevocationReason());
            assertEquals(now.plusMinutes(1), session.getRevokedAt());
        }

        @Test
        @DisplayName("Deve limpar historico e reativar ao renovar apos novo login")
        void renew_ShouldClearPreviousAndRevocation() {
            UserSession session = UserSession.create(userId, "hash-a", now, expiresAt);
            session.rotate("hash-b", now.plusMinutes(1), expiresAt);
            session.revoke(SessionRevocationReason.NEW_LOGIN, now.plusMinutes(2));

            session.renew("hash-c", now.plusMinutes(3), expiresAt);

            assertEquals("hash-c", session.getCurrentTokenHash());
            assertNull(session.getPreviousTokenHash());
            assertNull(session.getPreviousTokenRotatedAt());
            assertFalse(session.isRevoked());
            assertNull(session.getRevocationReason());
            assertFalse(session.matchesCurrent("hash-a"));
            assertFalse(session.matchesPrevious("hash-b"));
        }
    }
}
