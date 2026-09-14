package com.letraaletra.api.features.player.infrastructure.presentation.dto.response.player;

import com.letraaletra.api.features.participant.domain.EquippedCosmetic;
import com.letraaletra.api.features.player.domain.effect.PlayerEffect;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.InventoryResponse;

import java.util.List;

public record PlayerResponse(
        String id,
        String nickname,
        List<EquippedCosmetic> cosmeticsEquipped,
        int score,
        List<InventoryResponse> inventory,
        List<PlayerEffect> effects
) {
}
