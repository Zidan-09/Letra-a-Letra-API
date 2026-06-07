package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ReconnectParticipantResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateDTOMapper;

public class ReconnectParticipantMapper {
    public static ReconnectParticipantInput toInput(String user, String session) {
        return new ReconnectParticipantInput(
                user,
                session
        );
    }

    public static ReconnectParticipantResponse toResponse(ReconnectParticipantOutput output) {
        return new ReconnectParticipantResponse(
                GameStateDTOMapper.toGlobalDto(output.game())
        );
    }
}
