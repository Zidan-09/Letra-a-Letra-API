package com.letraaletra.api.features.participant.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameStateResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

@JsonTypeName("PARTICIPANT_RECONNECTED")
public record ReconnectParticipantResponse(
        GameResponse room,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        GameStateResponse gameState
) implements WsResponse {
}
