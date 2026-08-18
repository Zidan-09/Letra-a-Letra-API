package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;

@JsonTypeName("DISCARD_POWER")
public record DiscardPowerWsRequest(
        String gameId,
        String powerId
) implements WsRequest {
    @Override
    public String getAudit() {
        return "discarded the power " + powerId;
    }
}
