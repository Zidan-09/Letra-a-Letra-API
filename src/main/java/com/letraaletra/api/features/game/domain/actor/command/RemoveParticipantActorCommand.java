package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

import java.util.Optional;
import java.util.UUID;

public class RemoveParticipantActorCommand implements ActorCommand<Optional<Game>> {
    private final UserRepository userRepository;
    private final UUID userId;

    public RemoveParticipantActorCommand(
            UserRepository userRepository,
            UUID userId
    ) {
        this.userRepository = userRepository;
        this.userId = userId;
    }

    @Override
    public Optional<Game> execute(Game game) {
        User user = userRepository.find(userId).orElse(null);
        validateUser(user);

        validateGame(game);

        Participant participant = game.getParticipantByUserId(userId);

        if (participant == null || participant.isConnected()) return Optional.empty();

        user.leaveGame();
        game.remove(userId);

        userRepository.save(user);

        return Optional.of(game);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }
}
