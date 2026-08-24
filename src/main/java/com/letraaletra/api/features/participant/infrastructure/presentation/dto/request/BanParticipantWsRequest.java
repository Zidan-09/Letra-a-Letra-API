package com.letraaletra.api.features.participant.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("BAN_PARTICIPANT")
public record BanParticipantWsRequest(
        @NotBlank
        String gameId,

        @NotBlank
        String participantId
) implements WsRequest {
        @Override
        public String getAudit() {
                return "ban participant from game";
        }
}
