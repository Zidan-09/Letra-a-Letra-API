package com.letraaletra.api.features.participant.application.output;

import com.letraaletra.api.domain.game.Game;

public record BanParticipantOutput(
        String token,
        Game game
) {
}
