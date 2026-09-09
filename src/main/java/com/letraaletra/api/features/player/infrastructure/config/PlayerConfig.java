package com.letraaletra.api.features.player.infrastructure.config;

import com.letraaletra.api.features.game.application.port.GameOverFinalizer;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.usecase.DiscardPowerUseCase;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayerConfig {
    @Bean
    public UseCase<PlayerActionInput, PlayerActionOutput> playerActionUseCase(
            TurnTimeoutManager turnTimeoutManager,
            GameActorManager gameActorManager,
            GameOverFinalizer gameOverFinalizer,
            TransactionalExecutorService transactions
            ) {
        return new TransactionalUseCase<>(
                new PlayerActionUseCase(
                        turnTimeoutManager,
                        gameActorManager,
                        gameOverFinalizer
                ),
                transactions
        );
    }

    @Bean
    public UseCase<DiscardPowerInput, DiscardPowerOutput> discardPowerUseCase(
            GameActorManager gameActorManager,
            TurnTimeoutManager turnTimeoutManager,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new DiscardPowerUseCase(gameActorManager, turnTimeoutManager),
                transactions
        );
    }
}
