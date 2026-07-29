package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.participant.ParticipantResponse;

public class ParticipantResponseMapper {
    public static ParticipantResponse toResponse(Participant participant) {
        return new ParticipantResponse(
                participant.getUserId().toString(),
                participant.getNickname(),
                participant.getCosmeticsEquipped(),
                participant.getRole()
        );
    }
}
