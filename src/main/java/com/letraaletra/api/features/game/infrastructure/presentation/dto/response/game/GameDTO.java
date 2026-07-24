package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game;

import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.participant.ParticipantDTO;

import java.util.List;
import java.util.Map;

public record GameDTO(
    String gameId,
    String gameName,
    GameType type,
    GameStatus status,
    List<ParticipantDTO> participants,
    Map<Integer, String> positions
) {
}
