package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.features.user.application.output.EquippedItem;
import com.letraaletra.api.features.user.application.port.UserEquippedItemsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEquippedItemsService implements UserEquippedItemsProvider {
    private final InventoryRepository inventoryRepository;
    private final ItemDefinitionRepository itemDefinitionRepository;

    @Override
    public List<EquippedItem> equipped(UUID ownerId) {
        if (ownerId == null) {
            return List.of();
        }
        List<UserItem> items = inventoryRepository.findItemsByOwner(ownerId);
        List<EquippedItem> out = new ArrayList<>();
        for (UserItem item : items) {
            if (!item.isEquipped()) {
                continue;
            }
            ItemDefinition definition = itemDefinitionRepository.findById(item.getDefinitionId()).orElse(null);
            if (definition == null || !definition.isApplicableTo(ItemContext.PROFILE)) {
                continue;
            }
            out.add(new EquippedItem(item, definition));
        }
        return out;
    }

    @Override
    public Map<UUID, List<EquippedItem>> equippedFor(Collection<UUID> ownerIds) {
        Map<UUID, List<EquippedItem>> result = new HashMap<>();
        if (ownerIds == null || ownerIds.isEmpty()) {
            return result;
        }
        for (UUID ownerId : ownerIds) {
            result.put(ownerId, equipped(ownerId));
        }
        return result;
    }
}
