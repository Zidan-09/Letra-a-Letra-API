package com.letraaletra.api.features.participant.application.output;

import com.letraaletra.api.features.game.domain.Game;

import java.util.UUID;

public record DisconnectParticipantOutput(
        UUID user,
        Game game
) {
}
