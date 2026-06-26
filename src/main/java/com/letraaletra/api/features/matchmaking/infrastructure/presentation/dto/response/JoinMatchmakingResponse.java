package com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingStatus;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("MATCHMAKING_GAME")
public record JoinMatchmakingResponse(
        MatchmakingStatus status
) implements WsResponse {
}
