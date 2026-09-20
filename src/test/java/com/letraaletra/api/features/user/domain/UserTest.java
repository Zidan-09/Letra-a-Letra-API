package com.letraaletra.api.features.user.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.user.domain.ban.BanType;
import com.letraaletra.api.features.user.domain.effect.ActiveEffects;
import com.letraaletra.api.features.user.domain.ban.exception.UserAlreadyWasBannedException;
import com.letraaletra.api.features.user.domain.ban.exception.UserDoesNotHaveBanException;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;

class UserTest {

    private User user;
    private UUID gameId;
    private final UserFactory userFactory = new UserFactory();

    @BeforeEach
    void setUp() {
        user = userFactory.createLocal("PlayerOne", "test@test.com", "hash");
        gameId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Testes de Fluxo de Jogo (Room/Matchmaking)")
    class GameFlowTests {

        @Test
        @DisplayName("Deve permitir entrar em um jogo válido")
        void shouldEnterGameSuccessfully() {
            assertTrue(user.isNotInGame());
            assertNull(user.getCurrentGameId());

            user.enterGame(gameId);

            assertFalse(user.isNotInGame());
            assertEquals(gameId, user.getCurrentGameId());
        }

        @Test
        @DisplayName("Deve lançar GameNotFoundException se o gameId for nulo")
        void shouldThrowExceptionWhenGameIdIsNull() {
            assertThrows(GameNotFoundException.class, () -> user.enterGame(null));
        }

        @Test
        @DisplayName("Deve lançar UserAlreadyInGameException se o usuário já estiver em uma partida")
        void shouldThrowExceptionWhenUserIsAlreadyInGame() {
            user.enterGame(gameId);

            assertThrows(UserAlreadyInGameException.class, () -> user.enterGame(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Deve limpar o currentGameId ao sair do jogo")
        void shouldClearGameIdOnLeave() {
            user.enterGame(gameId);
            assertNotNull(user.getCurrentGameId());

            user.leaveGame();

            assertTrue(user.isNotInGame());
            assertNull(user.getCurrentGameId());
        }
    }

    @Nested
    @DisplayName("Testes de Banimento")
    class BanTests {

        @Test
        @DisplayName("Usuário recém-criado não está banido e banInfo nunca é nulo")
        void newUserShouldNotBeBannedAndBanInfoShouldNeverBeNull() {
            assertFalse(user.isBanned());
            assertNotNull(user.getBanInfo());
            assertNull(user.getBanInfo().type());
        }

        @Test
        @DisplayName("Deve banir usuário permanentemente quando não há expiração")
        void shouldBanPermanentlyWhenNoExpiration() {
            user.ban(null, "comportamento tóxico");

            assertTrue(user.isBanned());
            assertEquals(BanType.PERMANENT, user.getBanInfo().type());
            assertEquals("comportamento tóxico", user.getBanInfo().reason());
        }

        @Test
        @DisplayName("Deve banir usuário temporariamente quando há expiração")
        void shouldBanTemporarilyWithExpiration() {
            LocalDateTime expiresAt = LocalDateTime.now().plusDays(3);

            user.ban(expiresAt, "suspeita");

            assertTrue(user.isBanned());
            assertEquals(BanType.TEMPORARY, user.getBanInfo().type());
            assertEquals(expiresAt, user.getBanInfo().expiresAt());
        }

        @Test
        @DisplayName("Deve lançar UserAlreadyWasBannedException ao banir usuário já banido")
        void shouldThrowWhenBanningAlreadyBannedUser() {
            user.ban(null, "primeiro ban");

            assertThrows(UserAlreadyWasBannedException.class, () -> user.ban(null, "segundo ban"));
        }

        @Test
        @DisplayName("Deve remover o ban mantendo a invariante de banInfo nunca nulo")
        void unbanShouldKeepBanInfoNonNull() {
            user.ban(LocalDateTime.now().plusDays(1), "motivo");

            user.unban();

            assertFalse(user.isBanned());
            assertNotNull(user.getBanInfo(), "banInfo deve permanecer não nulo após unban");
            assertNull(user.getBanInfo().type());

            assertDoesNotThrow(() -> user.isBanned());
        }

        @Test
        @DisplayName("Deve lançar UserDoesNotHaveBanException ao desbanir usuário sem ban")
        void shouldThrowWhenUnbanningUserWithoutBan() {
            assertThrows(UserDoesNotHaveBanException.class, () -> user.unban());
        }

        @Test
        @DisplayName("Deve permitir banir novamente após unban (ciclo completo)")
        void shouldAllowRebanAfterUnban() {
            user.ban(null, "primeiro ban");
            user.unban();

            LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
            assertDoesNotThrow(() -> user.ban(expiresAt, "segundo ban"));

            assertTrue(user.isBanned());
            assertEquals(BanType.TEMPORARY, user.getBanInfo().type());
        }

        @Test
        @DisplayName("restore deve normalizar banInfo nulo para estado de não banido")
        void restoreShouldNormalizeNullBanInfo() {
            User restored = User.restore(
                    UUID.randomUUID(),
                    "restored",
                    "restored@test.com",
                    "hash",
                    UUID.randomUUID(),
                    null,
                    null,
                    null,
                    user.getStats(),
                    user.getWallet(),
                    ActiveEffects.create(),
                    LocalDateTime.now()
            );

            assertFalse(restored.isBanned());
            assertNotNull(restored.getBanInfo());
            assertDoesNotThrow(restored::isBanned);
        }
    }

    @Nested
    @DisplayName("Testes do modelo sem inventário (Etapa 8a2)")
    class NoInventoryTests {

        @Test
        @DisplayName("Usuário recém-criado possui stats e wallet sem inventário acoplado")
        void newUserHasStatsAndWalletWithoutInventory() {
            assertNotNull(user.getStats());
            assertNotNull(user.getWallet());
        }

        @Test
        @DisplayName("Usuário recém-criado possui efeitos ativos vazio")
        void newUserHasEmptyActiveEffects() {
            assertNotNull(user.getActiveEffects());
            assertTrue(user.getActiveEffects().isEmpty());
        }

        @Test
        @DisplayName("restore sem inventário preserva identidade e núcleo")
        void restoreWithoutInventoryKeepsCore() {
            User restored = User.restore(
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getTokenVersion(),
                    user.getGoogleId(),
                    user.getCurrentGameId(),
                    user.getBanInfo(),
                    user.getStats(),
                    user.getWallet(),
                    user.getActiveEffects(),
                    user.getCreatedAt()
            );

            assertEquals(user.getUserId(), restored.getUserId());
            assertEquals(user.getUsername(), restored.getUsername());
            assertEquals(user.getEmail(), restored.getEmail());
        }
    }
}