package com.letraaletra.api.features.game.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonTypeName("CREATE_GAME")
public record CreateGameWsRequest(
        @NotBlank
        String name,

        @Valid
        @NotNull
        RoomSettingsDTO settings
) implements WsRequest {
}
