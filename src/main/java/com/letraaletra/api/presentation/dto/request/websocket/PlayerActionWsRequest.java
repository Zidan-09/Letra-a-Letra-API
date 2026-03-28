package com.letraaletra.api.presentation.dto.request.websocket;

import com.letraaletra.api.presentation.dto.request.websocket.playeractions.PlayerActionDTO;
import jakarta.validation.constraints.NotBlank;

public record PlayerActionWsRequest(
        @NotBlank
        String tokenGameId,

        @NotBlank
        PlayerActionDTO action
) implements WsRequestDTO {
}
