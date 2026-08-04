package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.actor.result.LeftGameResult;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class LeftGameActorCommand implements ActorCommand<LeftGameResult> {
    private final UserRepository userRepository;
    private final String session;

    public LeftGameActorCommand(
            UserRepository userRepository,
            String session
    ) {
        this.userRepository = userRepository;
        this.session = session;
    }

    @Override
    public LeftGameResult execute(Game game) {
        Participant participant = game.getParticipants().findBySession(session);

        UUID participantId = participant.getUserId();
        User user = userRepository.find(participantId)
                .orElseThrow(UserNotFoundException::new);

        if (game.getGameStatus() == GameStatus.WAITING) {
            game.remove(participantId);
            user.leaveGame();

            if (game.getParticipants().isEmpty()) {
                game.setGameStatus(GameStatus.CLOSED);
            }

            userRepository.save(user);

            return new LeftGameResult(
                    game,
                    Optional.empty()
            );
        }

        if (participant.isSpectator()) {
            game.remove(participantId);
            user.leaveGame();

            userRepository.save(user);

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

        userRepository.save(user);

        return new LeftGameResult(
                game,
                gameOver
        );
    }
}
