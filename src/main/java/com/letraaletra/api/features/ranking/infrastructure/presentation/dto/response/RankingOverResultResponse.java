package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.ranking.RankingOverResponse;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("RANKING_OVER")
public record RankingOverResultResponse(
            RankingOverResponse  data
) implements WsResponse {
}
