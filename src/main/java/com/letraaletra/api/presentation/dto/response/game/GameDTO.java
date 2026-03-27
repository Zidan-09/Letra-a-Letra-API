package com.letraaletra.api.presentation.dto.response.game;

import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;

import java.util.List;

public record GameDTO(
    String tokenGameId,
    String gameName,
    List<ParticipantDTO> participants
) {
}
