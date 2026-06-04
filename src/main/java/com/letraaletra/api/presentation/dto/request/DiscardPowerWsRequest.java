package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;

@JsonTypeName("DISCARD_POWER")
public record DiscardPowerWsRequest(
        String tokenGameId,
        String powerId
) implements WsRequest {
}
