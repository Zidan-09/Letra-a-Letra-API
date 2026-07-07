package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.application.input.CloseRoomInput;
import com.letraaletra.api.features.game.application.output.CloseRoomOutput;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.RoomCloseReasons;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;

import java.util.UUID;

public class CloseRoomDueToTimeoutService implements UseCase<CloseRoomInput, CloseRoomOutput> {
    private final UserRepository userRepository;
    private final ActorManager<Game> actorManager;
    private final GameRepository gameRepository;

    public CloseRoomDueToTimeoutService(
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            GameRepository gameRepository
    ) {
        this.userRepository = userRepository;
        this.actorManager = actorManager;
        this.gameRepository = gameRepository;
    }

    public CloseRoomOutput execute(CloseRoomInput input) {
        Game game = input.game();

        game.getParticipants().forEach(p -> {
            UUID userId = p.getUserId();

            User user = userRepository.find(userId).orElse(null);
            validateUser(user);

            user.leaveGame();

            userRepository.save(user);
        });

        game.setGameStatus(GameStatus.CANCELED);

        actorManager.remove(game.getId());
        gameRepository.save(game);

        return buildOutput(game);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private CloseRoomOutput buildOutput(Game game) {
        return new CloseRoomOutput(
                game,
                "ROOM_CLOSED",
                RoomCloseReasons.INACTIVITY
        );
    }
}
