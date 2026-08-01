package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.application.port.CloseRoomService;
import com.letraaletra.api.features.game.domain.CloseRoomResult;
import com.letraaletra.api.features.game.domain.actor.command.CloseGameActorCommand;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.RoomCloseReasons;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CloseRoomDueToTimeoutService implements CloseRoomService {
    private final UserRepository userRepository;
    private final ActorManager<Game> actorManager;
    private final GameRepository gameRepository;

    @Override
    public CloseRoomResult close(Game game) {
        Actor actor = actorManager.get(game.getId());

        CompletableFuture<Game> future = actor.enqueueCommand(new CloseGameActorCommand(
                userRepository
        ));

        game = future.join();

        actorManager.remove(game.getId());
        gameRepository.save(game);

        return new CloseRoomResult(
                game,
                "ROOM_CLOSED",
                RoomCloseReasons.INACTIVITY
        );
    }
}
