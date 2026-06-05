package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.KickParticipantInput;
import com.letraaletra.api.features.participant.application.output.KickParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.KickParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.KickParticipantResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KickParticipantMapper {
    @Autowired
    private GameDTOMapper gameDTOMapper;

    public KickParticipantInput toCommand(KickParticipantWsRequest request, String userId) {
        return new KickParticipantInput(
                request.tokenGameId(),
                request.participantId(),
                userId
        );
    }

    public KickParticipantResponse toResponseDTO(KickParticipantOutput output) {
        return new KickParticipantResponse(
                gameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
