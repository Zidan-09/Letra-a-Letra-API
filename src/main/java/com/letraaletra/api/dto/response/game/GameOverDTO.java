package com.letraaletra.api.dto.response.game;

import com.letraaletra.api.dto.response.player.PlayerDTO;

import java.util.List;

public record GameOverDTO(
        List<PlayerDTO> players
) {
}
