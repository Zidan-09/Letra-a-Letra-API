package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.actor.output.LeftGameResult;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.exception.UserNotInGameException;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.game.domain.service.GameOver;

import java.util.Optional;
import java.util.UUID;

public class LeftGameActorCommand implements ActorCommand<LeftGameResult> {
    private final String session;

    public LeftGameActorCommand(String session) {
        this.session = session;
    }

    @Override
    public LeftGameResult execute(Game game) {
        Participant participant = game.findBySession(session);
        validateParticipant(participant);

        UUID participantId = participant.getUserId();

        if (game.getGameStatus().equals(GameStatus.WAITING)) {
            return new LeftGameResult(
                    game,
                    participant.getUserId(),
                    game.getParticipants().isEmpty(),
                    Optional.empty()
            );
        }

        Optional<GameOver> gameOver = game.getGameState()
                .gameOverBecausePlayerLeft(participantId);

        game.remove(participantId);

        return new LeftGameResult(
                game,
                participantId,
                game.getParticipants().isEmpty(),
                gameOver
        );
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }
}
