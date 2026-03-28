package com.letraaletra.api.presentation.dto.request.websocket;

import jakarta.validation.constraints.NotBlank;

public record LeftGameWsRequest(
        @NotBlank
        String tokenGameId
) implements WsRequestDTO {}
