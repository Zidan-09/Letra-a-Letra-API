package com.letraaletra.api.features.participant.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;

@JsonTypeName("PARTICIPANT_RECONNECTED")
public record ReconnectParticipantResponse(
        GameStateDTO data
) implements WsResponse {
}
