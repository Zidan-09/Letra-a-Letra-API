package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.actor.result.RemoveParticipantResult;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.user.domain.User;

import java.util.Optional;
import java.util.UUID;

public class RemoveDisconnectedParticipantActorCommand implements ActorCommand<RemoveParticipantResult> {
    private final User user;

    public RemoveDisconnectedParticipantActorCommand(
            User user
    ) {
        this.user = user;
    }

    @Override
    public RemoveParticipantResult execute(Game game) {
        UUID userId = user.getUserId();

        Participant participant = game.getParticipants()
                .getParticipantByUserId(userId);

        if (game.getGameStatus() == GameStatus.WAITING) {
            game.remove(userId);
            user.leaveGame();

            if (game.getParticipants().isEmpty()) {
                game.setGameStatus(GameStatus.CLOSED);
            }

            return new RemoveParticipantResult(
                    game,
                    Optional.empty()
            );
        }

        if (participant.isSpectator()) {
            game.remove(userId);
            user.leaveGame();

            return new RemoveParticipantResult(
                    game,
                    Optional.empty()
            );
        }

        Optional<GameOver> gameOver = game.getGameState()
                .gameOverBecauseDisconnection(userId);

        game.remove(userId);
        user.leaveGame();

        if (gameOver.isPresent()) {
            if (game.getGameType() == GameType.CUSTOM) {
                game.setGameStatus(GameStatus.WAITING);

            } else {
                game.setGameStatus(GameStatus.CLOSED);
            }
        }

        return new RemoveParticipantResult(
                game,
                gameOver
        );
    }
}
