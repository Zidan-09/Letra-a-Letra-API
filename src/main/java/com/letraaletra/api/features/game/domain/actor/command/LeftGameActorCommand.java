package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.actor.result.LeftGameResult;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.features.user.domain.User;

import java.util.Optional;
import java.util.UUID;

public class LeftGameActorCommand implements ActorCommand<LeftGameResult> {
    private final User user;
    private final String session;

    public LeftGameActorCommand(
            User user,
            String session
    ) {
        this.user = user;
        this.session = session;
    }

    @Override
    public LeftGameResult execute(Game game) {
        Participant participant = game.getParticipants().findBySession(session);

        UUID participantId = user.getUserId();

        if (game.getGameStatus() == GameStatus.WAITING) {
            game.remove(participantId);
            user.leaveGame();

            if (game.getParticipants().isEmpty()) {
                game.setGameStatus(GameStatus.CLOSED);
            }

            return new LeftGameResult(
                    game,
                    Optional.empty()
            );
        }

        if (participant.isSpectator()) {
            game.remove(participantId);
            user.leaveGame();

            return new LeftGameResult(
                    game,
                    Optional.empty()
            );
        }

        Optional<GameOver> gameOver = game.getGameState()
                .gameOverBecausePlayerLeft(participantId);

        game.remove(participantId);
        user.leaveGame();

        if (gameOver.isPresent()) {
            if (game.getGameType() == GameType.CUSTOM) {
                game.setGameStatus(GameStatus.WAITING);

            } else {
                game.setGameStatus(GameStatus.CLOSED);
            }
        }

        return new LeftGameResult(
                game,
                gameOver
        );
    }
}
