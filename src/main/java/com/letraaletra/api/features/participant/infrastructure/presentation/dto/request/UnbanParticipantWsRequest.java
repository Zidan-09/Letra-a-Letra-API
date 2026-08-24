package com.letraaletra.api.features.participant.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("UNBAN_PARTICIPANT")
public record UnbanParticipantWsRequest(
        @NotBlank
        String gameId,

        @NotBlank
        String userId
) implements WsRequest {
        @Override
        public String getAudit() {
                return "unbanned user from game";
        }
}
