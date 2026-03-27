package com.letraaletra.api.presentation.dto.request.websocket;

public record LeftGameWsRequest(
        String tokenGameId
) implements WsRequestDTO {}
