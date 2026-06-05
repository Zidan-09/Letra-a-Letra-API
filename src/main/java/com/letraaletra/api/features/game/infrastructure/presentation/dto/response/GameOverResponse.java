package com.letraaletra.api.features.game.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("GAME_OVER")
public record GameOverResponse(
        GameOverDTO data
) implements WsResponse {
}
