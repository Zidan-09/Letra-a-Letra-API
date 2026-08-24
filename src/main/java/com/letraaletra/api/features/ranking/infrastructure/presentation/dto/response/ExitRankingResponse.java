package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.ranking.domain.RankingMessages;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("EXIT_RANKING")
public record ExitRankingResponse(
        RankingMessages message
) implements WsResponse {
}
