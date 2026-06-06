package com.letraaletra.api.features.participant.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;

@JsonTypeName("PARTICIPANT_BANNED")
public record BanParticipantResponse(
        GameDTO data
) implements WsResponse {
}
