package com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.GameStateDTO;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.MatchmakingStatus;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

import java.time.Instant;

@JsonTypeName("MATCHMAKING_GAME")
public record JoinMatchmakingResponse(
        MatchmakingStatus status,
        Instant turnEndsAt,
        String tokenGameId,
        GameStateDTO data
) implements WsResponse {
}
