package com.letraaletra.api.presentation.dto.request.websocket;

public record JoinGameWsRequest(
        String tokenGameId
) implements WsRequestDTO {
}
