package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.player.PlayerResponse;

public class PlayerResponseMapper {
    public static PlayerResponse toResponse(Player player, Participant participant) {
        return new PlayerResponse(
                participant.getUserId().toString(),
                participant.getNickname(),
                participant.getCosmeticsEquipped(),
                player.getScore(),
                player.getInventory().keySet().stream()
                        .map(key -> InventoryResponseMapper.toResponse(key, player.getInventory().get(key)))
                        .toList(),
                player.getEffects()
        );
    }
}
