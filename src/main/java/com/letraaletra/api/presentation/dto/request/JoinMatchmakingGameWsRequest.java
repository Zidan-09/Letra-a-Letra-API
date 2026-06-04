package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.domain.game.state.GameMode;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;

@JsonTypeName("MATCHMAKING_GAME")
public record JoinMatchmakingGameWsRequest(
        GameMode gameMode
) implements WsRequest {
}
