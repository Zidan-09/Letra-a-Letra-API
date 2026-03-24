package com.letraaletra.api.dto.request.websocket;

import com.letraaletra.api.dto.request.websocket.playeractions.PlayerAction;

public record PlayerActionWsRequest(
    String tokenGameId,
    PlayerAction action
) implements WsRequestDTO {
}
