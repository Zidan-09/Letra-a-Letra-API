package com.letraaletra.api.features.items.domain;

import com.letraaletra.api.features.items.domain.exception.InvalidItemStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Item Unit Tests")
class  ItemTest {

    private EquippableItem avatar() {
        return EquippableItem.create(
                "Blue Avatar",
                EquippableContext.PROFILE,
                EquippableCategory.AVATAR,
                "avatars/blue.png"
        );
    }

    @Test
    @DisplayName("create deve gerar id, versao 1 e disponivel")
    void createShouldGenerateDefaults() {
        EquippableItem item = avatar();

        assertNotNull(item.getId());
        assertEquals(1, item.getVersion());
        assertTrue(item.isAvailable());
        assertEquals("Blue Avatar", item.getName());
    }

    @Test
    @DisplayName("restore deve preservar id, versao e disponibilidade")
    void restoreShouldPreserveFields() {
        UUID id = UUID.randomUUID();

        EquippableItem item = EquippableItem.restore(
                id,
                "Blue Avatar",
                3,
                false,
                EquippableContext.PROFILE,
                EquippableCategory.AVATAR,
                "avatars/blue.png"
        );

        assertEquals(id, item.getId());
        assertEquals(3, item.getVersion());
        assertFalse(item.isAvailable());
    }

    @Test
    @DisplayName("versao so pode avancar via incrementVersion")
    void versionShouldOnlyMoveForward() {
        EquippableItem item = avatar();

        item.incrementVersion();

        assertEquals(2, item.getVersion());
    }

    @Test
    @DisplayName("disable/enable devem alternar disponibilidade sem remover o item")
    void availabilityShouldToggleWithoutRemoving() {
        EquippableItem item = avatar();

        item.disable();
        assertFalse(item.isAvailable());
        assertThrows(InvalidItemStatusException.class, item::disable);

        item.enable();
        assertTrue(item.isAvailable());
        assertThrows(InvalidItemStatusException.class, item::enable);

        assertEquals("Blue Avatar", item.getName());
    }
}
