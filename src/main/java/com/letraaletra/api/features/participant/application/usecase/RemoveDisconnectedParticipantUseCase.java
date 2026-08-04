package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.RemoveDisconnectedParticipantActorCommand;
import com.letraaletra.api.features.game.domain.actor.result.RemoveParticipantResult;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.participant.application.input.RemoveDisconnectedParticipantInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

public class RemoveDisconnectedParticipantUseCase implements UseCase<RemoveDisconnectedParticipantInput, Void> {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GameTimeoutManager gameTimeoutManager;
    private final ActorManager<Game> actorManager;
    private final GameOverService gameOverService;

    public RemoveDisconnectedParticipantUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            GameTimeoutManager gameTimeoutManager,
            ActorManager<Game> actorManager,
            GameOverService gameOverService
    ) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameTimeoutManager = gameTimeoutManager;
        this.actorManager = actorManager;
        this.gameOverService = gameOverService;
    }

    @Override
    @Transactional
    public Void execute(RemoveDisconnectedParticipantInput input) {
        Actor actor = actorManager.get(input.gameId());

        User user = userRepository.find(input.userId())
                .orElseThrow(UserNotFoundException::new);

        CompletableFuture<RemoveParticipantResult> future = actor.enqueueCommand(new RemoveDisconnectedParticipantActorCommand(
                user
        ));

        RemoveParticipantResult result = future.join();

        if (result.game().getGameStatus().equals(GameStatus.WAITING)) {
            gameTimeoutManager.start(result.game());

        } else if (result.game().getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(result.game().getId());

        }

        result.gameOver().ifPresent(over -> gameOverService.handle(result.game(), over));

        userRepository.save(user);
        gameRepository.save(result.game());

        return null;
    }
}
