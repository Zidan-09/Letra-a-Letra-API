package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.game.CloseRoomCommand;
import com.letraaletra.api.application.output.game.CloseRoomOutput;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.RoomCloseReasons;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;

public class CloseRoomDueToTimeoutUseCase {
    private final UserRepository userRepository;
    private final ActorManager actorManager;
    private final GameRepository gameRepository;

    public CloseRoomDueToTimeoutUseCase(
            UserRepository userRepository,
            ActorManager actorManager,
            GameRepository gameRepository
    ) {
        this.userRepository = userRepository;
        this.actorManager = actorManager;
        this.gameRepository = gameRepository;
    }

    public CloseRoomOutput execute(CloseRoomCommand command) {
        Game game = command.game();

        game.getParticipants().forEach(p -> {
            String userId = p.getUserId();

            User user = userRepository.find(userId);

            user.leaveGame();

            userRepository.save(user);
        });

        game.setGameStatus(GameStatus.CANCELED);

        actorManager.remove(game.getId());
        gameRepository.save(game);

        return buildReturn(game);
    }

    private CloseRoomOutput buildReturn(Game game) {
        return new CloseRoomOutput(
                game,
                "ROOM_CLOSED",
                RoomCloseReasons.INACTIVITY
        );
    }
}
