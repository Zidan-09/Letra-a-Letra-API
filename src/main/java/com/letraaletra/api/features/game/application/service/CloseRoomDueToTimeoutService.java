package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.application.input.CloseRoomInput;
import com.letraaletra.api.features.game.application.output.CloseRoomOutput;
import com.letraaletra.api.features.game.domain.actor.command.CloseGameActorCommand;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.RoomCloseReasons;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

import java.util.concurrent.CompletableFuture;

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

        Actor actor = actorManager.get(game.getId());

        CompletableFuture<Game> future = actor.enqueueCommand(new CloseGameActorCommand(
                userRepository
        ));

        game = future.join();

        actorManager.remove(game.getId());
        gameRepository.save(game);

        return new CloseRoomOutput(
                game,
                "ROOM_CLOSED",
                RoomCloseReasons.INACTIVITY
        );
    }
}
