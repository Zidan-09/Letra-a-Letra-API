package com.letraaletra.api.features.participant.application.output;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.domain.Participant;

public record ModerationContext(
        Game game,
        Participant participant
) {
}
