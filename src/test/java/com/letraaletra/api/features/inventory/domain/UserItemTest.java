package com.letraaletra.api.features.inventory.domain;

import com.letraaletra.api.features.inventory.domain.exception.InsufficientQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserItem Unit Tests")
class UserItemTest {

    private final UUID ownerId = UUID.randomUUID();
    private final UUID definitionId = UUID.randomUUID();

    @Test
    @DisplayName("create deve iniciar nao equipado, com acquiredAt e sem expiracao")
    void createShouldInitializeDefaults() {
        UserItem item = UserItem.create(ownerId, definitionId, 2);

        assertEquals(ownerId, item.getOwnerId());
        assertEquals(definitionId, item.getItemId());
        assertEquals(2, item.getQuantity());
        assertFalse(item.isEquipped());
        assertNotNull(item.getAcquiredAt());
        assertNull(item.getExpiresAt());
    }

    @Test
    @DisplayName("create com quantidade invalida ou ids nulos deve falhar")
    void createWithInvalidDataShouldFail() {
        assertThrows(InvalidQuantityException.class, () -> UserItem.create(ownerId, definitionId, 0));
        assertThrows(InvalidQuantityException.class, () -> UserItem.create(null, definitionId, 1));
        assertThrows(InvalidQuantityException.class, () -> UserItem.create(ownerId, null, 1));
    }

    @Test
    @DisplayName("restore deve preservar todos os campos")
    void restoreShouldPreserveAllFields() {
        LocalDateTime acquiredAt = LocalDateTime.now().minusDays(1);
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        UserItem item = UserItem.restore(ownerId, definitionId, 3, true, acquiredAt, expiresAt);

        assertEquals(3, item.getQuantity());
        assertTrue(item.isEquipped());
        assertEquals(acquiredAt, item.getAcquiredAt());
        assertEquals(expiresAt, item.getExpiresAt());
    }

    @Test
    @DisplayName("increase e decrease devem ajustar a quantidade")
    void increaseAndDecreaseShouldAdjustQuantity() {
        UserItem item = UserItem.create(ownerId, definitionId, 1);

        item.increase(2);
        assertEquals(3, item.getQuantity());

        item.decrease(2);
        assertEquals(1, item.getQuantity());
    }

    @Test
    @DisplayName("increase ou decrease com valor menor que 1 deve falhar")
    void increaseOrDecreaseBelowOneShouldFail() {
        UserItem item = UserItem.create(ownerId, definitionId, 1);

        assertThrows(InvalidQuantityException.class, () -> item.increase(0));
        assertThrows(InvalidQuantityException.class, () -> item.decrease(0));
    }

    @Test
    @DisplayName("decrease acima do saldo deve falhar sem alterar a quantidade")
    void decreaseAboveBalanceShouldFail() {
        UserItem item = UserItem.create(ownerId, definitionId, 1);

        assertThrows(InsufficientQuantityException.class, () -> item.decrease(2));
        assertEquals(1, item.getQuantity());
    }

    @Test
    @DisplayName("markEquipped e markUnequipped devem alternar o estado")
    void equipFlagsShouldToggle() {
        UserItem item = UserItem.create(ownerId, definitionId, 1);

        item.markEquipped();
        assertTrue(item.isEquipped());

        item.markUnequipped();
        assertFalse(item.isEquipped());
    }
}
