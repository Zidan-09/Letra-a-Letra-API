package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.application.output.actor.LeftGameResult;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.exception.UserNotInGameException;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.game.domain.service.GameOverResult;

public class LeftGameActorCommand implements ActorCommand<LeftGameResult> {
    private final String session;

    public LeftGameActorCommand(String session) {
        this.session = session;
    }

    @Override
    public LeftGameResult execute(Game game) {
        Participant participant = game.findBySession(session);
        validateParticipant(participant);

        game.remove(participant.getUserId());

        if (game.getGameStatus().equals(GameStatus.WAITING)) {
            return new LeftGameResult(
                    game,
                    participant.getUserId(),
                    game.getParticipants().isEmpty(),
                    null
            );
        }

        GameOverResult gameOverResult = game.getGameState().gameOverChecker();

        return new LeftGameResult(
                game,
                participant.getUserId(),
                game.getParticipants().isEmpty(),
                gameOverResult
        );
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }
}
