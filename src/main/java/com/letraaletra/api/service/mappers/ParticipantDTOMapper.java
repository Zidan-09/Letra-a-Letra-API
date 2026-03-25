package com.letraaletra.api.service.mappers;

import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.dto.response.participant.ParticipantDTO;
import org.springframework.stereotype.Component;

@Component
public class ParticipantDTOMapper {
    public ParticipantDTO toDTO(Participant participant) {
        return new ParticipantDTO(
                participant.getUserId(),
                participant.getNickname(),
                participant.getAvatar(),
                participant.getRole()
        );
    }
}
