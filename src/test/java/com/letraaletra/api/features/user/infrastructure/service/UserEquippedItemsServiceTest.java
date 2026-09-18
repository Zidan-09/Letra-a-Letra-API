package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.items.domain.EquippableItem;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.EquippableContext;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.features.user.application.output.EquippedItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserEquippedItemsService Unit Tests")
class UserEquippedItemsServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private UserEquippedItemsService service;

    @Test
    @DisplayName("Deve retornar vazio quando ownerId for nulo")
    void equipped_WhenOwnerIsNull_ShouldReturnEmpty() {
        assertTrue(service.equipped(null).isEmpty());
    }

    @Test
    @DisplayName("Deve retornar apenas itens equipados aplicáveis a PROFILE")
    void equipped_WhenItemsExist_ShouldFilterEquippedProfile() {
        UUID ownerId = UUID.randomUUID();
        UUID equippedId = UUID.randomUUID();
        UUID unequippedId = UUID.randomUUID();
        UUID matchOnlyId = UUID.randomUUID();

        when(inventoryRepository.findItemsByOwner(ownerId)).thenReturn(List.of(
                UserItem.restore(ownerId, equippedId, 1, true, LocalDateTime.now(), null),
                UserItem.restore(ownerId, unequippedId, 1, false, LocalDateTime.now(), null),
                UserItem.restore(ownerId, matchOnlyId, 1, true, LocalDateTime.now(), null)));

        when(itemRepository.findById(equippedId)).thenReturn(Optional.of(item(equippedId, ItemCategory.AVATAR, EquippableContext.PROFILE)));
        when(itemRepository.findById(matchOnlyId)).thenReturn(Optional.of(item(matchOnlyId, ItemCategory.BOARD_SKIN, EquippableContext.MATCH)));

        List<EquippedItem> result = service.equipped(ownerId);

        assertEquals(1, result.size());
        assertEquals(equippedId, result.getFirst().item().getId());
    }

    @Test
    @DisplayName("Deve agrupar equipados por owner")
    void equippedFor_WhenOwnersProvided_ShouldGroupByOwner() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID definitionId = UUID.randomUUID();

        when(inventoryRepository.findItemsByOwner(first)).thenReturn(List.of(
                UserItem.restore(first, definitionId, 1, true, LocalDateTime.now(), null)));
        when(inventoryRepository.findItemsByOwner(second)).thenReturn(List.of());
        when(itemRepository.findById(definitionId)).thenReturn(Optional.of(item(definitionId, ItemCategory.AVATAR, EquippableContext.PROFILE)));

        Map<UUID, List<EquippedItem>> result = service.equippedFor(List.of(first, second));

        assertEquals(1, result.get(first).size());
        assertTrue(result.get(second).isEmpty());
    }

    private Item item(UUID id, ItemCategory category, EquippableContext context) {
        return EquippableItem.restore(id, "Item", 1, true, context, category, "asset.webp");
    }
}
