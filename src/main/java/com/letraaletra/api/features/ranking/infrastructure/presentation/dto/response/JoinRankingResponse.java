package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingStatus;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("RANKING_GAME")
public record JoinRankingResponse(
        MatchmakingStatus status
) implements WsResponse {
}
