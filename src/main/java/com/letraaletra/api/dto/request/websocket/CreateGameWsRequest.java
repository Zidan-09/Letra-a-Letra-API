package com.letraaletra.api.dto.request.websocket;

import com.letraaletra.api.domain.game.GameSettings;

public record CreateGameWsRequest(
        String name,
        GameSettings gameSettings
) implements WsRequestDTO {
}
