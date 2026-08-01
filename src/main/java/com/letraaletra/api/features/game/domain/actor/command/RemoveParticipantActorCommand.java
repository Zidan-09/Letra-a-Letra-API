package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;

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
        User user = userRepository.find(userId)
                .orElseThrow(UserNotFoundException::new);

        Participant participant = game.getParticipants().getParticipantByUserId(userId);

        if (participant.isConnected()) return Optional.empty();

        user.leaveGame();
        game.remove(userId);

        if (game.getParticipants().getAmountPlayers() == 0) game.setGameStatus(GameStatus.CLOSED);

        userRepository.save(user);

        return Optional.of(game);
    }
}
