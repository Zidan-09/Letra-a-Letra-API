package com.letraaletra.api.features.game.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("PARTICIPANT_JOIN")
public record JoinGameResponse(
        GameResponse data
) implements WsResponse {
}
