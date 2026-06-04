package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;
import com.letraaletra.api.presentation.dto.response.game.MatchmakingStatus;
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
