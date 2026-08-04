package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.application.port.ExpireTurnService;
import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.domain.actor.command.ExpireTurnActorCommand;
import com.letraaletra.api.features.game.domain.ExpireTurnTimeoutResult;
import com.letraaletra.api.features.game.domain.actor.result.ExpireTurnResult;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ExpireTurnTimeoutService implements ExpireTurnService {
    private final ActorManager<Game> gameActorManager;
    private final GameOverService gameOverService;
    private final UserRepository userRepository;

    @Override
    public Optional<ExpireTurnTimeoutResult> expire(UUID gameId, int version) {
        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<Optional<ExpireTurnResult>> future = actor.enqueueCommand(
                new ExpireTurnActorCommand(version)
        );

        Optional<ExpireTurnResult> result = future.join();

        result.ifPresent(r -> r.gameOver().ifPresent(gameOver -> {
                    User user = userRepository.find(r.whoPassed())
                            .orElseThrow(UserNotFoundException::new);

                    user.leaveGame();

                    userRepository.save(user);

                    gameOverService.handle(r.game(), gameOver);
                }
        ));

        return result.flatMap(this::buildOutput);
    }

    private Optional<ExpireTurnTimeoutResult> buildOutput(com.letraaletra.api.features.game.domain.actor.result.ExpireTurnResult result) {
        return Optional.of(
                new ExpireTurnTimeoutResult(
                        "TURN_EXPIRED",
                        result.whoPassed(),
                        result.game().getGameState().currentPlayerTurn(),
                        result.game(),
                        result.gameOver()
                )
        );
    }
}
