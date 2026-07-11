package com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;

@JsonTypeName("EXIT_MATCHMAKING")
public record ExitMatchmakingGameWsRequest()
        implements WsRequest {
    @Override
    public String getAudit() {
        return "exited of matchmaking queue";
    }
}
