package com.letraaletra.api.presentation.dto.request.websocket;

import com.letraaletra.api.presentation.dto.request.websocket.playeractions.PlayerActionDTO;

public record PlayerActionWsRequest(
    String tokenGameId,
    PlayerActionDTO action
) implements WsRequestDTO {
}
