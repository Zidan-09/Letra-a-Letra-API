package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.participant.ParticipantDTO;

public class ParticipantDTOMapper {
    public static ParticipantDTO toDTO(Participant participant) {
        return new ParticipantDTO(
                participant.getUserId().toString(),
                participant.getNickname(),
                participant.getCosmeticsEquipped(),
                participant.getRole()
        );
    }
}
