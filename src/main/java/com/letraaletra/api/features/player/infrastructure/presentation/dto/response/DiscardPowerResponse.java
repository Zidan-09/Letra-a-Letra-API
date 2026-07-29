package com.letraaletra.api.features.player.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameStateResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("POWER_DISCARDED")
public record DiscardPowerResponse(
        GameStateResponse data
) implements WsResponse {
}
