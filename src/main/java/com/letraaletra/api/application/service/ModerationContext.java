package com.letraaletra.api.application.service;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;

public record ModerationContext(
        Game game,
        Participant participant
) {
}
