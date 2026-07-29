package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.ranking;

public record RankingOverResponse(
        RankedMatchResult winner,
        RankedMatchResult loser
) {
}
