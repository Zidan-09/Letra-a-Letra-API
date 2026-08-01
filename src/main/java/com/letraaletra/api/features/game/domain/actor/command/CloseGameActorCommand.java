package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

import java.util.UUID;

public class CloseGameActorCommand implements ActorCommand<Game> {
    private final UserRepository userRepository;

    public CloseGameActorCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Game execute(Game game) {
        for (Participant participant : game.getParticipants().getParticipants()) {
            UUID userId = participant.getUserId();

            User user = userRepository.find(userId)
                    .orElseThrow(UserNotFoundException::new);

            user.leaveGame();

            userRepository.save(user);
        }

        game.setGameStatus(GameStatus.CANCELED);

        return game;
    }
}
