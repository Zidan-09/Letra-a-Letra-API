package com.letraaletra.api.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.request.game.GameSettingsDTO;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("START_GAME")
public record StartGameWsRequest(
        @NotBlank
        String tokenGameId,

        @NotBlank
        GameSettingsDTO settings
) implements WsRequestDTO {
}
