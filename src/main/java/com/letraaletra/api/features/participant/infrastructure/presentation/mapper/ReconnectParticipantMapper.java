package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ReconnectParticipantResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReconnectParticipantMapper {
    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    public ReconnectParticipantInput toCommand(String user, String session) {
        return new ReconnectParticipantInput(
                user,
                session
        );
    }

    public ReconnectParticipantResponse toResponseDTO(ReconnectParticipantOutput output) {
        return new ReconnectParticipantResponse(
                gameStateDTOMapper.toAllDTO(output.game())
        );
    }
}
