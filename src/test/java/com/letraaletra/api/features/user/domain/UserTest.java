package com.letraaletra.api.features.user.domain;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.user.domain.exceptions.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Testes de Gerenciamento de Cosméticos (Inventory)")
    class CosmeticInventoryTests {

        private UUID itemId1;
        private UUID itemId2;
        private UUID itemId3;

        private Cosmetic item1;
        private Cosmetic item2;
        private Cosmetic item3;

        @BeforeEach
        void setup() {
            item1 = Cosmetic.create("item-1", CosmeticTypes.AVATAR, "any-path");
            item2 = Cosmetic.create("item-2", CosmeticTypes.AVATAR, "any-path");
            item3 = Cosmetic.create("item-", CosmeticTypes.BANNER, "any-path");
        }

        @Test
        @DisplayName("Deve equipar o cosmético desejado e desequipar outros do mesmo tipo")
        void shouldEquipCosmeticAndUnequipOthersOfSameType() {
            Inventory inventory = user.getInventory();

            itemId1 = item1.getId();
            itemId2 = item2.getId();
            itemId3 = item3.getId();

            inventory.unlock(item1);
            inventory.unlock(item2);
            inventory.unlock(item3);
            inventory.equipCosmetic(itemId1);
            inventory.equipCosmetic(itemId3);

            inventory.equipCosmetic(itemId2);

            List<InventoryItem> updatedInventory = user.getInventory().getItems();

            InventoryItem updatedItem1 = updatedInventory.stream().filter(i -> i.cosmeticId().equals(itemId1)).findFirst().orElseThrow();
            InventoryItem updatedItem2 = updatedInventory.stream().filter(i -> i.cosmeticId().equals(itemId2)).findFirst().orElseThrow();
            InventoryItem updatedItem3 = updatedInventory.stream().filter(i -> i.cosmeticId().equals(itemId3)).findFirst().orElseThrow();

            assertFalse(updatedItem1.equipped(), "O antigo avatar equipado (id-1) deveria ter sido desequipado");
            assertTrue(updatedItem2.equipped(), "O novo avatar (id-2) deveria estar equipado");
            assertTrue(updatedItem3.equipped(), "Itens de categorias diferentes (BANNER) não deveriam ser afetados");
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException ao tentar equipar um item que o usuário não possui")
        void shouldThrowExceptionWhenItemNotFoundInInventory() {
            assertThrows(IllegalArgumentException.class, () -> user.getInventory().equipCosmetic(UUID.randomUUID()));
        }
    }
}