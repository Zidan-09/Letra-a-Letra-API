package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerDTOMapper {
    @Autowired
    private InventoryDTOMapper inventoryDTOMapper;

    public PlayerResponse toDTO(Player player, Participant participant) {
        return new PlayerResponse(
                participant.getUserId(),
                participant.getNickname(),
                participant.getAvatar(),
                player.getScore(),
                player.getInventory().keySet().stream()
                        .map(key -> inventoryDTOMapper.toDTO(key, player.getInventory().get(key)))
                        .toList(),
                player.getEffects()
        );
    }
}
