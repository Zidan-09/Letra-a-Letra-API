package com.letraaletra.api.features.user.domain.inventory;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.user.domain.inventory.exception.InvalidUserCosmeticSelectedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Inventory Movements Unit Tests")
class InventoryMovementsTest {

    private Cosmetic cosmetic(String name, CosmeticTypes type) {
        Cosmetic cosmetic = mock(Cosmetic.class);
        when(cosmetic.getId()).thenReturn(UUID.randomUUID());
        lenientIdAndName(cosmetic, name);
        when(cosmetic.getType()).thenReturn(type);
        return cosmetic;
    }

    private void lenientIdAndName(Cosmetic cosmetic, String name) {
        when(cosmetic.getName()).thenReturn(name);
    }

    @Test
    @DisplayName("unlock deve retornar movimento ACQUIRED com equipped=false")
    void unlockShouldReturnAcquiredMovement() {
        Inventory inventory = Inventory.create();
        Cosmetic cosmetic = cosmetic("Blue Avatar", CosmeticTypes.AVATAR);

        List<InventoryMovement> movements = inventory.unlock(cosmetic);

        assertEquals(1, movements.size());
        InventoryMovement movement = movements.get(0);
        assertEquals(InventoryChangeKind.ACQUIRED, movement.kind());
        assertNull(movement.equippedBefore());
        assertFalse(movement.equippedAfter());
        assertEquals(cosmetic.getId(), movement.cosmeticId());
    }

    @Nested
    @DisplayName("equip")
    class Equip {

        @Test
        @DisplayName("equipar item deve gerar EQUIPPED para o alvo e UNEQUIPPED para o anterior do mesmo tipo")
        void equipShouldGenerateEquippedAndUnequippedMovements() {
            Inventory inventory = Inventory.create();

            Cosmetic oldHat = cosmetic("Old Avatar", CosmeticTypes.AVATAR);
            Cosmetic newHat = cosmetic("New Avatar", CosmeticTypes.AVATAR);

            inventory.unlock(oldHat);
            inventory.unlock(newHat);
            inventory.equipCosmetic(oldHat.getId());

            List<InventoryMovement> movements = inventory.equipCosmetic(newHat.getId());

            assertEquals(2, movements.size());

            InventoryMovement unequipped = movements.stream()
                    .filter(m -> m.kind() == InventoryChangeKind.UNEQUIPPED)
                    .findFirst()
                    .orElseThrow();

            InventoryMovement equipped = movements.stream()
                    .filter(m -> m.kind() == InventoryChangeKind.EQUIPPED)
                    .findFirst()
                    .orElseThrow();

            assertEquals(oldHat.getId(), unequipped.cosmeticId());
            assertTrue(unequipped.equippedBefore());
            assertFalse(unequipped.equippedAfter());

            assertEquals(newHat.getId(), equipped.cosmeticId());
            assertFalse(equipped.equippedBefore());
            assertTrue(equipped.equippedAfter());
        }

        @Test
        @DisplayName("re-equipar o mesmo item n??o deve gerar movimentos")
        void reEquipSameItemShouldNotGenerateMovements() {
            Inventory inventory = Inventory.create();
            Cosmetic hat = cosmetic("Avatar", CosmeticTypes.AVATAR);

            inventory.unlock(hat);

            List<InventoryMovement> first = inventory.equipCosmetic(hat.getId());
            List<InventoryMovement> second = inventory.equipCosmetic(hat.getId());

            assertEquals(1, first.size());
            assertTrue(second.isEmpty());
        }
    }

    @Nested
    @DisplayName("removeFromInventory")
    class Remove {

        @Test
        @DisplayName("remover item n??o equipado deve gerar apenas REMOVED")
        void removingUnequippedItemShouldOnlyGenerateRemoved() {
            Inventory inventory = Inventory.create();
            Cosmetic hat = cosmetic("Avatar", CosmeticTypes.AVATAR);

            inventory.unlock(hat);

            List<InventoryMovement> movements = inventory.removeFromInventory(hat.getId());

            assertEquals(1, movements.size());
            assertEquals(InventoryChangeKind.REMOVED, movements.get(0).kind());
            assertFalse(movements.get(0).equippedBefore());
        }

        @Test
        @DisplayName("remover item equipado deve gerar REMOVED e auto-equip de fallback (EQUIPPED)")
        void removingEquippedItemShouldAutoEquipFallback() {
            Inventory inventory = Inventory.create();

            Cosmetic equippedHat = cosmetic("Equipped Avatar", CosmeticTypes.AVATAR);
            Cosmetic fallbackHat = cosmetic("Fallback Avatar", CosmeticTypes.AVATAR);
            Cosmetic shirt = cosmetic("Frame", CosmeticTypes.FRAME);

            inventory.unlock(equippedHat);
            inventory.unlock(fallbackHat);
            inventory.unlock(shirt);
            inventory.equipCosmetic(equippedHat.getId());
            inventory.equipCosmetic(shirt.getId());

            List<InventoryMovement> movements = inventory.removeFromInventory(equippedHat.getId());

            assertEquals(2, movements.size());

            InventoryMovement removed = movements.get(0);
            assertEquals(InventoryChangeKind.REMOVED, removed.kind());
            assertTrue(removed.equippedBefore());

            InventoryMovement autoEquipped = movements.get(1);
            assertEquals(InventoryChangeKind.EQUIPPED, autoEquipped.kind());
            assertEquals(fallbackHat.getId(), autoEquipped.cosmeticId());
        }

        @Test
        @DisplayName("remover cosm??tico inexistente deve lan??ar exce????o")
        void removingUnknownCosmeticShouldThrow() {
            Inventory inventory = Inventory.create();

            assertThrows(InvalidUserCosmeticSelectedException.class,
                    () -> inventory.removeFromInventory(UUID.randomUUID()));
        }
    }
}
