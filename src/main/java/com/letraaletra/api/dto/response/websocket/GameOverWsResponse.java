package com.letraaletra.api.dto.response.websocket;

import com.letraaletra.api.dto.response.game.GameOverDTO;

public record GameOverWsResponse(
        GameOverDTO data
) implements WsResponseDTO {
}
