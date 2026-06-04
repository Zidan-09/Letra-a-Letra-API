package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionRequest;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("PLAYER_ACTION")
public record PlayerActionWsRequest(
        @NotBlank
        String tokenGameId,

        @Valid
        @NotNull
        PlayerActionRequest action
) implements WsRequest {
}
