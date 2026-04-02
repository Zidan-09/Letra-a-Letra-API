package com.letraaletra.api.application.context;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;

public record PlayerGameContext(
    Game game,
    Participant participant
) {
}