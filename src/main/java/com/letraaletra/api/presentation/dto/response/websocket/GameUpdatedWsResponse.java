package com.letraaletra.api.presentation.dto.response.websocket;

import com.letraaletra.api.presentation.dto.response.game.GameDTO;

public record GameUpdatedWsResponse(
        GameDTO data
) implements WsResponseDTO {
}
