package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
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
    private final ItemRepository itemRepository;

    @Override
    public List<EquippedItem> equipped(UUID ownerId) {
        if (ownerId == null) {
            return List.of();
        }
        List<UserItem> items = inventoryRepository.findItemsByOwner(ownerId);
        List<EquippedItem> out = new ArrayList<>();
        for (UserItem owned : items) {
            if (!owned.isEquipped()) {
                continue;
            }
            Item item = itemRepository.findById(owned.getItemId()).orElse(null);
            if (!(item instanceof EquippableItem equippable) || equippable.getContext() != EquippableContext.PROFILE) {
                continue;
            }
            out.add(new EquippedItem(owned, item));
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
