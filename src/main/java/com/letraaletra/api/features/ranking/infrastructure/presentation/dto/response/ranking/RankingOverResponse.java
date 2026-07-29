package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.ranking;

import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankedMatchResult;

public record RankingOverResponse(
        RankedMatchResult winner,
        RankedMatchResult loser
) {
}
