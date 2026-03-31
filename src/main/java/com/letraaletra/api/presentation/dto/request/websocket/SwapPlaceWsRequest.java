package com.letraaletra.api.presentation.dto.request.websocket;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SwapPlaceWsRequest(
        @NotBlank
        String tokenGameId,

        @NotNull
        @Min(0)
        @Max(6)
        Integer position
) implements WsRequestDTO {
}
