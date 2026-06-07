package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ReconnectParticipantResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateDTOMapper;
import org.springframework.stereotype.Component;

@Component
public class ReconnectParticipantMapper {
    public ReconnectParticipantInput toCommand(String user, String session) {
        return new ReconnectParticipantInput(
                user,
                session
        );
    }

    public ReconnectParticipantResponse toResponseDTO(ReconnectParticipantOutput output) {
        return new ReconnectParticipantResponse(
                GameStateDTOMapper.toGlobalDto(output.game())
        );
    }
}
