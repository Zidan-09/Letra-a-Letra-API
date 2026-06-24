package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.KickParticipantInput;
import com.letraaletra.api.features.participant.application.output.KickParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.KickParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.KickParticipantResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameDTOMapper;

import java.util.UUID;

public class KickParticipantMapper {
    public static KickParticipantInput toInput(KickParticipantWsRequest request, String userId) {
        return new KickParticipantInput(
                UUID.fromString(request.gameId()),
                UUID.fromString(request.participantId()),
                UUID.fromString(userId)
        );
    }

    public static KickParticipantResponse toResponse(KickParticipantOutput output) {
        return new KickParticipantResponse(
                GameDTOMapper.toDTO(output.game())
        );
    }
}
