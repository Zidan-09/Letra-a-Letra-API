package com.letraaletra.api.dto.response.websocket;

public record ErrorWsResponse(
        String message
) implements WsResponseDTO {}
