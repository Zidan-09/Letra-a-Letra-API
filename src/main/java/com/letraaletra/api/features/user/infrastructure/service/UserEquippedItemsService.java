package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.InventoryItemResponseMapper;
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
public class UserEquippedItemsService {
    private final InventoryRepository inventoryRepository;
    private final ItemDefinitionRepository itemDefinitionRepository;

    public List<InventoryItemResponse> equippedResponses(UUID ownerId) {
        if (ownerId == null) {
            return List.of();
        }
        List<UserItem> items = inventoryRepository.findItemsByOwner(ownerId);
        List<InventoryItemResponse> out = new ArrayList<>();
        for (UserItem item : items) {
            if (!item.isEquipped()) {
                continue;
            }
            ItemDefinition definition = itemDefinitionRepository.findById(item.getDefinitionId()).orElse(null);
            if (definition == null || !definition.isApplicableTo(ItemContext.PROFILE)) {
                continue;
            }
            out.add(InventoryItemResponseMapper.toResponse(item, definition));
        }
        return out;
    }

    public Map<UUID, List<InventoryItemResponse>> equippedResponsesFor(Collection<UUID> ownerIds) {
        Map<UUID, List<InventoryItemResponse>> result = new HashMap<>();
        if (ownerIds == null || ownerIds.isEmpty()) {
            return result;
        }
        for (UUID ownerId : ownerIds) {
            result.put(ownerId, equippedResponses(ownerId));
        }
        return result;
    }
}
