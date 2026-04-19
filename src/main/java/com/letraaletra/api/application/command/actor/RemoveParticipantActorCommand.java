package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;

public class RemoveParticipantActorCommand implements ActorCommand<Void> {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ActorManager actorManager;
    private final String userId;

    public RemoveParticipantActorCommand(
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager actorManager,
            String userId
    ) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.actorManager = actorManager;
        this.userId = userId;
    }

    @Override
    public Void execute(Game game) {
        User user = userRepository.find(userId).orElse(null);
        validateUser(user);

        validateGame(game);

        Participant participant = game.getParticipantByUserId(userId);

        if (participant == null || participant.isConnected()) return null;

        user.leaveGame();
        game.remove(userId);

        userRepository.save(user);

        if (game.getParticipants().isEmpty()) {
            actorManager.remove(game.getId());
        } else {
            gameRepository.save(game);
        }

        return null;
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
