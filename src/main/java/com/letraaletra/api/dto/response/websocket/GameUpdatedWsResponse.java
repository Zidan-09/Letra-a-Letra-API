package com.letraaletra.api.dto.response.websocket;

import com.letraaletra.api.dto.response.game.GameDTO;

public record GameUpdatedWsResponse(
        GameDTO data
) implements WsResponseDTO {
}
