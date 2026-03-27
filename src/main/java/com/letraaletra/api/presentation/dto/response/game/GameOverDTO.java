package com.letraaletra.api.presentation.dto.response.game;

import com.letraaletra.api.presentation.dto.response.player.PlayerDTO;

import java.util.List;

public record GameOverDTO(
        List<PlayerDTO> players
) {
}
