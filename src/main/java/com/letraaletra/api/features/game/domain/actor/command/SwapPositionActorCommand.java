package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.UserNotInGameException;
import com.letraaletra.api.features.participant.domain.Participant;

public class SwapPositionActorCommand implements ActorCommand<Game> {
    private final String user;
    private final int position;

    public SwapPositionActorCommand(String user, int position) {
        this.user = user;
        this.position = position;
    }

    @Override
    public Game execute(Game game) {
        Participant participant = game.getParticipantByUserId(user);

        validateParticipant(participant);

        game.changePosition(user, position);

        return game;
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }
}
