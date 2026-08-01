package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.actor.command.RemoveDisconnectedParticipantActorCommand;
import com.letraaletra.api.features.game.domain.actor.output.RemoveParticipantResult;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.participant.application.input.RemoveDisconnectedParticipantInput;
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

    public RemoveDisconnectedParticipantUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            GameTimeoutManager gameTimeoutManager,
            ActorManager<Game> actorManager
    ) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameTimeoutManager = gameTimeoutManager;
        this.actorManager = actorManager;
    }

    @Override
    @Transactional
    public Void execute(RemoveDisconnectedParticipantInput input) {
        Actor actor = actorManager.get(input.gameId());

        CompletableFuture<RemoveParticipantResult> future = actor.enqueueCommand(new RemoveDisconnectedParticipantActorCommand(
                userRepository, input.userId()
        ));

        RemoveParticipantResult result = future.join();

        if (result.game().getGameStatus().equals(GameStatus.WAITING)) {
            gameTimeoutManager.start(result.game());

        } else if (result.game().getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(result.game().getId());

        }

        gameRepository.save(result.game());

        return null;
    }
}
