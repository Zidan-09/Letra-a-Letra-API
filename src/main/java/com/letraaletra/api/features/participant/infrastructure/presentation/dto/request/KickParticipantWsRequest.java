package com.letraaletra.api.features.participant.infrastructure.presentation.dto.request;

import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.constraints.NotBlank;

public record KickParticipantWsRequest(
        @NotBlank
        String gameId,

        @NotBlank
        String participantId
) implements WsRequest {
}
