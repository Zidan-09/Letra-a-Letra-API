package com.letraaletra.api.presentation.dto.mappers;

import com.letraaletra.api.domain.player.Player;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.presentation.dto.response.player.PlayerDTO;
import org.springframework.stereotype.Component;

@Component
public class PlayerDTOMapper {
    public PlayerDTO toDTO(Player player, User user) {
        return new PlayerDTO(
                user.getId(),
                user.getNickname(),
                user.getAvatar(),
                player.getScore(),
                player.getInventory()
        );
    }
}
