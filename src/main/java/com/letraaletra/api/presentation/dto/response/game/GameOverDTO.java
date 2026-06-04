package com.letraaletra.api.presentation.dto.response.game;

import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerResponse;

public record GameOverDTO(
        PlayerResponse winner,
        PlayerResponse loser
) {
}
