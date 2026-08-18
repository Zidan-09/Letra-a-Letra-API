package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.player.PlayerResponse;

public record RankedMatchResult(
        PlayerResponse player,
        int previousRankingPoints,
        int pointsChanged,
        int currentRankingPoints
) {
}
