package com.letraaletra.api.features.ranking.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.ranking.RankedMatchResult;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankingOverResultResponse;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.ranking.RankingOverResponse;

public class RankingOverResultMapper {
    public static RankingOverResultResponse toResponse(RankedMatchResult winner, RankedMatchResult loser) {
        return new RankingOverResultResponse(
                new RankingOverResponse(
                        winner,
                        loser
                )
        );
    }
}
