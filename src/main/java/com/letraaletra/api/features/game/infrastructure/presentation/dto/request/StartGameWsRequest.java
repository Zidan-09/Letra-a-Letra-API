package com.letraaletra.api.features.game.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("START_GAME")
public record StartGameWsRequest(
        @NotBlank
        String gameId,

        @NotNull
        @Valid
        GameSettingsDTO settings
) implements WsRequest {
        @Override
        public String getAudit() {
                return "started game";
        }
}
