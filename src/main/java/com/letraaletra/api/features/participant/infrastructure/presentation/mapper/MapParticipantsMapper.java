package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ParticipantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MapParticipantsMapper {
    @Autowired
    private ParticipantDTOMapper participantDTOMapper;

    public List<ParticipantDTO> execute(Game game) {
        return game.getParticipants().stream()
                .map(participantDTOMapper::toDTO)
                .toList();
    }
}
