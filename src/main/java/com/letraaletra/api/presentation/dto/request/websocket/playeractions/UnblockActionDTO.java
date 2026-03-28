package com.letraaletra.api.presentation.dto.request.websocket.playeractions;

import com.letraaletra.api.presentation.dto.request.websocket.PositionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record UnblockActionDTO(
        @Valid
        @NotNull
        PositionDTO position
) implements PlayerActionDTO {
}
