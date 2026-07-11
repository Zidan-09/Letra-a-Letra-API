package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerDTO;

public record RankedMatchResult(
        PlayerDTO player,
        int previousRankingPoints,
        int pointsChanged,
        int currentRankingPoints
) {
}
