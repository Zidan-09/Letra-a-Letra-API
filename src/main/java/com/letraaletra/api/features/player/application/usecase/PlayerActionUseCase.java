package com.letraaletra.api.features.player.application.usecase;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.PlayerActionActorCommand;
import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.game.domain.actor.result.PlayerActionResult;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerActionUseCase implements UseCase<PlayerActionInput, PlayerActionOutput> {
    private final RoomTimeoutManager roomTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;
    private final GameOverService gameOverService;
    private final UserRepository userRepository;

    public PlayerActionUseCase(
            RoomTimeoutManager roomTimeoutManager,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager,
            GameOverService gameOverService,
            UserRepository userRepository
    ) {
        this.roomTimeoutManager = roomTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
        this.gameOverService = gameOverService;
        this.userRepository = userRepository;
    }

    @Override
    public PlayerActionOutput execute(PlayerActionInput input) {
        UUID gameId = UUID.fromString(input.gameId());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<PlayerActionResult> future = actor.enqueueCommand(new PlayerActionActorCommand(
                input.user(), input.action(), turnTimeoutManager
        ));

        PlayerActionResult result = future.join();

        if (result.game().getGameStatus().equals(GameStatus.WAITING)) {
            roomTimeoutManager.start(result.game());

        } else if (result.game().getGameStatus().equals(GameStatus.CLOSED)) {
            releaseParticipants(result.game());
            gameActorManager.remove(result.game().getId());
        }

        HandledGameOver handledGameOver = result.gameOver()
                .map(over -> gameOverService.handle(result.game(), over))
                .orElseGet(HandledGameOver::withoutRanking);

        return buildOutput(result, handledGameOver);
    }

    private void releaseParticipants(Game game) {
        List<User> userList = userRepository.findUsersById(game.getParticipants().getIds());

        userList.forEach(User::leaveGame);

        userRepository.saveAll(userList);
    }

    private PlayerActionOutput buildOutput(PlayerActionResult result, HandledGameOver handledGameOver) {
        return new PlayerActionOutput(
                result.game(),
                result.events(),
                result.gameOver(),
                handledGameOver
        );
    }
}
