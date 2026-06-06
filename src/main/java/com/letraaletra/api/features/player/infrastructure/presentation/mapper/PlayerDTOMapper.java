package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerDTOMapper {
    @Autowired
    private InventoryDTOMapper inventoryDTOMapper;

    public PlayerDTO toDTO(Player player, Participant participant) {
        return new PlayerDTO(
                participant.getUserId(),
                participant.getNickname(),
                participant.getCosmeticsEquipped(),
                player.getScore(),
                player.getInventory().keySet().stream()
                        .map(key -> inventoryDTOMapper.toDTO(key, player.getInventory().get(key)))
                        .toList(),
                player.getEffects()
        );
    }
}
