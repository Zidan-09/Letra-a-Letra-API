package com.letraaletra.api.presentation.dto.request.websocket;

public record StartGameWsRequest(
        String tokenGameId
) implements WsRequestDTO {
}
