package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("RANKING_OVER")
public record RankingOverResponse(
        RankingOverDTO data
) implements WsResponse {
}
