package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.participant.Participant;

public record ModerationContext(
        Game game,
        Participant participant
) {
}
