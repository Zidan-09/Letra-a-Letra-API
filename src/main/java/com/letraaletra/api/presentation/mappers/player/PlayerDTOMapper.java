package com.letraaletra.api.presentation.mappers.player;

import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.presentation.dto.response.player.PlayerDTO;
import org.springframework.stereotype.Component;

@Component
public class PlayerDTOMapper {
    public PlayerDTO toDTO(Player player, Participant participant) {
        return new PlayerDTO(
                participant.getUserId(),
                participant.getNickname(),
                participant.getAvatar(),
                player.getScore(),
                player.getInventory()
        );
    }
}
