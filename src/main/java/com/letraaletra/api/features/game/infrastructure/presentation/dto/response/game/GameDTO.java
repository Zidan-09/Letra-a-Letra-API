package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game;

import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.participant.ParticipantDTO;

import java.util.List;
import java.util.Map;

public record GameDTO(
    String tokenGameId,
    String gameName,
    List<ParticipantDTO> participants,
    Map<Integer, String> positions
) {
}
