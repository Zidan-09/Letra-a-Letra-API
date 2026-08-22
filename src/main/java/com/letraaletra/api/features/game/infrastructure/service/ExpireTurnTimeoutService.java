package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.application.output.ExpireTurnTimeoutResult;
import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.application.port.ExpireTurnService;
import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.domain.actor.command.ExpireTurnActorCommand;
import com.letraaletra.api.features.game.domain.actor.result.ExpireTurnResult;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;
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

        if (result.isEmpty()) {
            return Optional.empty();
        }

        ExpireTurnResult turnResult = result.get();

        HandledGameOver handled = HandledGameOver.withoutRanking();

        Optional<GameOver> gameOver = turnResult.gameOver();

        if (gameOver.isPresent()) {
            User user = userRepository.find(turnResult.whoPassed())
                    .orElseThrow(UserNotFoundException::new);

            user.leaveGame();

            userRepository.save(user);

            handled = gameOverService.handle(turnResult.game(), gameOver.get());
        }

        return Optional.of(buildOutput(turnResult, handled));
    }

    private ExpireTurnTimeoutResult buildOutput(ExpireTurnResult result, HandledGameOver handled) {
        return new ExpireTurnTimeoutResult(
                "TURN_EXPIRED",
                result.whoPassed(),
                result.game().getGameState().currentPlayerTurn(),
                result.game(),
                result.gameOver(),
                handled
        );
    }
}
