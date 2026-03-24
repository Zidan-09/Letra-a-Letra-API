package com.letraaletra.api.dto.request.websocket;

public record JoinGameWsRequest(
        String tokenGameId
) implements WsRequestDTO {
}
