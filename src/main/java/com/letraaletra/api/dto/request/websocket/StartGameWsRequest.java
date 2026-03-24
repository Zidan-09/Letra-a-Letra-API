package com.letraaletra.api.dto.request.websocket;

public record StartGameWsRequest(
        String tokenGameId
) implements WsRequestDTO {
}
