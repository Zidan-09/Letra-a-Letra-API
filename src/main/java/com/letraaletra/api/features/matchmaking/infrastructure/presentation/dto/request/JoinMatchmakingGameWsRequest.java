package com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;

@JsonTypeName("MATCHMAKING_GAME")
public record JoinMatchmakingGameWsRequest(
        GameMode gameMode
) implements WsRequest {
    @Override
    public String getAudit() {
        return "joined in matchmaking queue";
    }
}
