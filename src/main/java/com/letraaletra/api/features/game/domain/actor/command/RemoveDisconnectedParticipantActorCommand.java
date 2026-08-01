package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.actor.output.RemoveParticipantResult;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class RemoveDisconnectedParticipantActorCommand implements ActorCommand<RemoveParticipantResult> {
    private final UserRepository userRepository;
    private final UUID userId;

    public RemoveDisconnectedParticipantActorCommand(
            UserRepository userRepository,
            UUID userId
    ) {
        this.userRepository = userRepository;
        this.userId = userId;
    }

    @Override
    public RemoveParticipantResult execute(Game game) {
        Participant participant = game.getParticipants()
                .getParticipantByUserId(userId);

        User user = userRepository.find(userId)
                .orElseThrow(UserNotFoundException::new);

        if (game.getGameStatus() == GameStatus.WAITING) {
            game.remove(userId);
            user.leaveGame();

            if (game.getParticipants().isEmpty()) {
                game.setGameStatus(GameStatus.CLOSED);
            }

            userRepository.save(user);

            return new RemoveParticipantResult(
                    game,
                    Optional.empty()
            );
        }

        if (participant.isSpectator()) {
            game.remove(userId);
            user.leaveGame();

            userRepository.save(user);

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

        userRepository.save(user);

        return new RemoveParticipantResult(
                game,
                gameOver
        );
    }
}
