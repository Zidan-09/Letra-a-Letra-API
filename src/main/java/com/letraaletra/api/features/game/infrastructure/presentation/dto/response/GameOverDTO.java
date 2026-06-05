package com.letraaletra.api.features.game.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerResponse;

public record GameOverDTO(
        PlayerResponse winner,
        PlayerResponse loser
) {
}
