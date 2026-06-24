package com.letraaletra.api.features.participant.infrastructure.presentation.dto.request;

import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SwapPositionWsRequest(
        @NotBlank
        String gameId,

        @NotNull
        @Min(0)
        @Max(6)
        Integer position
) implements WsRequest {
}
