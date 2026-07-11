package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("PLAYER_ACTION")
public record PlayerActionWsRequest(
        @NotBlank
        String gameId,

        @Valid
        @NotNull
        PlayerActionRequest action
) implements WsRequest {
        @Override
        public String getAudit() {
                return "execute a " + action.getClass().getSimpleName();
        }
}
