package com.letraaletra.api.presentation.dto.request.websocket;

import jakarta.validation.constraints.NotBlank;

public record BanParticipantWsRequest(
        @NotBlank
        String tokenGameId,

        @NotBlank
        String participantId
) implements WsRequestDTO {
}
