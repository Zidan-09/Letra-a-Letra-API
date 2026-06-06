package com.letraaletra.api.features.player.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.player.domain.effect.PlayerEffect;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

import java.util.List;

public record PlayerDTO(
        String id,
        String nickname,
        List<InventoryItem> cosmeticsEquipped,
        int score,
        List<InventoryResponse> inventory,
        List<PlayerEffect> effects
) {
}
