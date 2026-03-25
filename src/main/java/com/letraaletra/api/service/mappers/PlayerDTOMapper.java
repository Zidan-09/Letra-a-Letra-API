package com.letraaletra.api.service.mappers;

import com.letraaletra.api.domain.player.Player;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.dto.response.player.PlayerDTO;
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
