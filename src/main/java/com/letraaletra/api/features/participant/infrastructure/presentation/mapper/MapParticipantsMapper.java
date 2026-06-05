package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ParticipantDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MapParticipantsMapper {
    public static List<ParticipantDTO> execute(Game game) {
        return game.getParticipants().stream()
                .map(ParticipantDTOMapper::toDTO)
                .toList();
    }
}
