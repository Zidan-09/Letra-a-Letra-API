package com.letraaletra.api.features.participant.application.output;

import com.letraaletra.api.domain.game.Game;

public record KickParticipantOutput(
        String token,
        Game game
) {
}
