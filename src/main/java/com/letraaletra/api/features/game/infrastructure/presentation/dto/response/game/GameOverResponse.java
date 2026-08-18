package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game;

import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.player.PlayerResponse;

public record GameOverResponse(
        PlayerResponse winner,
        PlayerResponse loser
) {
}
