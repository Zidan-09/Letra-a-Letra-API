package com.letraaletra.api.application.service;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.user.User;

public record PlayerGameContext(
    User user,
    Game game,
    Participant participant
) {
}