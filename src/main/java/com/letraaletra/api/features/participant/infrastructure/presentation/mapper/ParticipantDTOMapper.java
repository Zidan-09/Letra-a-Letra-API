package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.participant.ParticipantDTO;
import org.springframework.stereotype.Component;

@Component
public class ParticipantDTOMapper {
    public static ParticipantDTO toDTO(Participant participant) {
        return new ParticipantDTO(
                participant.getUserId(),
                participant.getNickname(),
                participant.getAvatar(),
                participant.getRole()
        );
    }
}
