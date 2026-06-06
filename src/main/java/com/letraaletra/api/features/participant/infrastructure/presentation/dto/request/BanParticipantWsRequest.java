package com.letraaletra.api.features.participant.infrastructure.presentation.dto.request;

import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.constraints.NotBlank;

public record BanParticipantWsRequest(
        @NotBlank
        String tokenGameId,

        @NotBlank
        String participantId
) implements WsRequest {
}
