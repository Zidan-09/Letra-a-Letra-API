package com.letraaletra.api.presentation.dto.request.websocket;

import com.letraaletra.api.presentation.dto.request.websocket.playeractions.PlayerActionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlayerActionWsRequest(
        @NotBlank
        String tokenGameId,

        @Valid
        @NotNull
        PlayerActionDTO action
) implements WsRequestDTO {
}
