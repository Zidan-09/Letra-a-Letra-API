package com.letraaletra.api.features.ranking.infrastructure.presentation.mapper;

import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankedMatchResult;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankingOverDTO;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankingOverResponse;

public class RankingOverMapper {
    public static RankingOverResponse toResponse(RankedMatchResult winner, RankedMatchResult loser) {
        return new RankingOverResponse(
                new RankingOverDTO(
                        winner,
                        loser
                )
        );
    }
}
