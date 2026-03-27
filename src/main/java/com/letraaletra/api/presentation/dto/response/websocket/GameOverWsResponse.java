package com.letraaletra.api.presentation.dto.response.websocket;

import com.letraaletra.api.presentation.dto.response.game.GameOverDTO;

public record GameOverWsResponse(
        GameOverDTO data
) implements WsResponseDTO {
}
