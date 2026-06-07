package com.letraaletra.api.features.user.domain;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.user.domain.exceptions.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private final UserFactory userFactory = new UserFactory();

    @BeforeEach
    void setUp() {
        user = userFactory.createLocal("user-1", "PlayerOne", "test@test.com", "hash");
    }

    @Nested
    @DisplayName("Testes de Fluxo de Jogo (Room/Matchmaking)")
    class GameFlowTests {

        @Test
        @DisplayName("Deve permitir entrar em um jogo válido")
        void shouldEnterGameSuccessfully() {
            assertTrue(user.isNotInGame());
            assertNull(user.getCurrentGameId());

            user.enterGame("game-uuid-123");

            assertFalse(user.isNotInGame());
            assertEquals("game-uuid-123", user.getCurrentGameId());
        }

        @Test
        @DisplayName("Deve lançar GameNotFoundException se o gameId for nulo ou vazio")
        void shouldThrowExceptionWhenGameIdIsNullOrEmpty() {
            assertThrows(GameNotFoundException.class, () -> user.enterGame(null));
            assertThrows(GameNotFoundException.class, () -> user.enterGame("   "));
        }

        @Test
        @DisplayName("Deve lançar UserAlreadyInGameException se o usuário já estiver em uma partida")
        void shouldThrowExceptionWhenUserIsAlreadyInGame() {
            user.enterGame("game-1");

            assertThrows(UserAlreadyInGameException.class, () -> user.enterGame("game-2"));
        }

        @Test
        @DisplayName("Deve limpar o currentGameId ao sair do jogo")
        void shouldClearGameIdOnLeave() {
            user.enterGame("game-1");
            assertNotNull(user.getCurrentGameId());

            user.leaveGame();

            assertTrue(user.isNotInGame());
            assertNull(user.getCurrentGameId());
        }
    }

    @Nested
    @DisplayName("Testes de Gerenciamento de Cosméticos (Inventory)")
    class CosmeticInventoryTests {

        @Test
        @DisplayName("Deve equipar o cosmético desejado e desequipar outros do mesmo tipo")
        void shouldEquipCosmeticAndUnequipOthersOfSameType() {
            CosmeticTypes typeAvatar = CosmeticTypes.AVATAR;
            CosmeticTypes typeBanner = CosmeticTypes.BANNER;
            LocalDateTime now = LocalDateTime.now();

            InventoryItem item1 = new InventoryItem("id-1", "Avatar Caveira", typeAvatar, true, now);
            InventoryItem item2 = new InventoryItem("id-2", "Avatar Paladino", typeAvatar, false, now);
            InventoryItem itemDeOutroTipo = new InventoryItem("id-3", "Tabuleiro Natal", typeBanner, true, now);

            user.setInventory(new ArrayList<>(List.of(item1, item2, itemDeOutroTipo)));

            user.equipCosmetic("id-2");

            List<InventoryItem> updatedInventory = user.getInventory();

            InventoryItem updatedItem1 = updatedInventory.stream().filter(i -> i.cosmetic_id().equals("id-1")).findFirst().orElseThrow();
            InventoryItem updatedItem2 = updatedInventory.stream().filter(i -> i.cosmetic_id().equals("id-2")).findFirst().orElseThrow();
            InventoryItem updatedItem3 = updatedInventory.stream().filter(i -> i.cosmetic_id().equals("id-3")).findFirst().orElseThrow();

            assertFalse(updatedItem1.equipped(), "O antigo avatar equipado (id-1) deveria ter sido desequipado");
            assertTrue(updatedItem2.equipped(), "O novo avatar (id-2) deveria estar equipado");
            assertTrue(updatedItem3.equipped(), "Itens de categorias diferentes (BOARD) não deveriam ser afetados");
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException ao tentar equipar um item que o usuário não possui")
        void shouldThrowExceptionWhenItemNotFoundInInventory() {
            assertThrows(IllegalArgumentException.class, () -> user.equipCosmetic("id-inexistente"));
        }
    }
}