package com.letraaletra.api.presentation.dto.response.websocket;

import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;

public record GameStartedWsResponse(
        GameStateDTO data
) implements WsResponseDTO {
}
