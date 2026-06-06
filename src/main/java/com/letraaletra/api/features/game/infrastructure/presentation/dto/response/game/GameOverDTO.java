package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game;

import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerDTO;

public record GameOverDTO(
        PlayerDTO winner,
        PlayerDTO loser
) {
}
