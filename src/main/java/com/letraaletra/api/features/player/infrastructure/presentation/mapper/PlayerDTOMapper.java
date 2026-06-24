package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerDTO;

public class PlayerDTOMapper {
    public static PlayerDTO toDTO(Player player, Participant participant) {
        return new PlayerDTO(
                participant.getUserId().toString(),
                participant.getNickname(),
                participant.getCosmeticsEquipped(),
                player.getScore(),
                player.getInventory().keySet().stream()
                        .map(key -> InventoryDTOMapper.toDto(key, player.getInventory().get(key)))
                        .toList(),
                player.getEffects()
        );
    }
}
