package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ReconnectParticipantResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateResponseMapper;

import java.util.UUID;

public class ReconnectParticipantMapper {
    public static ReconnectParticipantInput toInput(String user, String session) {
        return new ReconnectParticipantInput(
                UUID.fromString(user),
                session
        );
    }

    public static ReconnectParticipantResponse toResponse(ReconnectParticipantOutput output) {
        return new ReconnectParticipantResponse(
                GameStateResponseMapper.toGlobalResponse(output.game())
        );
    }
}
