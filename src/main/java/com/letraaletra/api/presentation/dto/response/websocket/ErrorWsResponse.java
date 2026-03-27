package com.letraaletra.api.presentation.dto.response.websocket;

public record ErrorWsResponse(
        String message
) implements WsResponseDTO {}
