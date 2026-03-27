package com.letraaletra.api.presentation.dto.response.websocket;

import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;

public record GameStateUpdatedWsResponse(
        GameStateDTO data
) implements WsResponseDTO {
}
