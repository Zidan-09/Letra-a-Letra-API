package com.letraaletra.api.presentation.dto.request.websocket;

import jakarta.validation.constraints.NotBlank;

public record StartGameWsRequest(
        @NotBlank
        String tokenGameId
) implements WsRequestDTO {
}
