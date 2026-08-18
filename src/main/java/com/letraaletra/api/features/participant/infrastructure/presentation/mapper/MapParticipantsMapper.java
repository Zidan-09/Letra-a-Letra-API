package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.participant.ParticipantResponse;

import java.util.List;

public class MapParticipantsMapper {
    public static List<ParticipantResponse> map(Game game) {
        return game.getParticipants().getParticipants().stream()
                .map(ParticipantResponseMapper::toResponse)
                .toList();
    }
}
