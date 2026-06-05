package com.letraaletra.api.application.context;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.domain.Participant;

public record ModerationContext(
        Game game,
        Participant participant
) {
}
