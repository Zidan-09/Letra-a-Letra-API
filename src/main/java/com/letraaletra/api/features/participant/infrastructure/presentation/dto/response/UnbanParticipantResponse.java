package com.letraaletra.api.features.participant.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("PARTICIPANT_UNBANNED")
public record UnbanParticipantResponse(
            String gameId
) implements WsResponse {
}
