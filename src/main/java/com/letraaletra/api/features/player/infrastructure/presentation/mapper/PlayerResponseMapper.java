package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.player.PlayerResponse;

public class PlayerResponseMapper {
    public static PlayerResponse toResponse(Player player, Participant participant) {
        java.util.Map<String, com.letraaletra.api.features.game.domain.board.power.PowerType> powers =
                player.getInventory().getPowers();
        return new PlayerResponse(
                participant.getUserId().toString(),
                participant.getNickname(),
                participant.getCosmeticsEquipped(),
                player.getScore(),
                powers.keySet().stream()
                        .map(key -> InventoryResponseMapper.toResponse(key, powers.get(key)))
                        .toList(),
                player.getActiveEffects().getEffects()
        );
    }
}
