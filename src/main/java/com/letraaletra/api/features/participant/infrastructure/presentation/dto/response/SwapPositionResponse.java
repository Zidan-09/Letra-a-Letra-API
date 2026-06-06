package com.letraaletra.api.features.participant.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;

@JsonTypeName("POSITIONS_UPDATED")
public record SwapPositionResponse(
        GameDTO data
) implements WsResponse {
}
