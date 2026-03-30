package com.letraaletra.api.presentation.websocket;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.user.User;

public record PlayerGameContext(
    User user,
    Game game,
    Participant participant
) {
}