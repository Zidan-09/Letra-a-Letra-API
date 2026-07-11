package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response;

public record RankingOverDTO(
        RankedMatchResult winner,
        RankedMatchResult loser
) {
}
