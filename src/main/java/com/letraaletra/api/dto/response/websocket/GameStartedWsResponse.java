package com.letraaletra.api.dto.response.websocket;

import com.letraaletra.api.dto.response.game.GameStateDTO;

public record GameStartedWsResponse(
        GameStateDTO data
) implements WsResponseDTO {
}
