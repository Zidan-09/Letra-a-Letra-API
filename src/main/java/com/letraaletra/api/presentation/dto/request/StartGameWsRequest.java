package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.request.game.GameSettingsDTO;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("START_GAME")
public record StartGameWsRequest(
        @NotBlank
        String tokenGameId,

        @NotNull
        @Valid
        GameSettingsDTO settings
) implements WsRequest {
}
