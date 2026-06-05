package com.letraaletra.api.features.participant.application.output;

import com.letraaletra.api.features.game.domain.Game;

public record KickParticipantOutput(
        String token,
        Game game
) {
}
