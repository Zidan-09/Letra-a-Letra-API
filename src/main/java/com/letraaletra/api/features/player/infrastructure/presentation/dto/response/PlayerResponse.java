package com.letraaletra.api.features.player.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.player.domain.effect.PlayerEffect;

import java.util.List;

public record PlayerResponse(
        String id,
        String nickname,
        String avatar,
        int score,
        List<InventoryResponse> inventory,
        List<PlayerEffect> effects
) {
}
