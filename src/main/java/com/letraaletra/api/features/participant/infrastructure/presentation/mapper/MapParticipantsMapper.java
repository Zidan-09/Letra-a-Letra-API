package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.participant.ParticipantDTO;

import java.util.List;

public class MapParticipantsMapper {
    public static List<ParticipantDTO> execute(Game game) {
        return game.getParticipants().stream()
                .map(ParticipantDTOMapper::toDTO)
                .toList();
    }
}
