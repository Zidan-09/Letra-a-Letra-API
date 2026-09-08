package com.letraaletra.api.features.player.infrastructure.config;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.features.player.application.usecase.DiscardPowerUseCase;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayerConfig {
    @Bean
    public PlayerActionUseCase playerActionUseCase(
            TurnTimeoutManager turnTimeoutManager,
            GameActorManager gameActorManager,
            GameOverService gameOverService
            ) {
        return new PlayerActionUseCase(
                turnTimeoutManager,
                gameActorManager,
                gameOverService
        );
    }

    @Bean
    public DiscardPowerUseCase discardPowerUseCase(GameActorManager gameActorManager, TurnTimeoutManager turnTimeoutManager) {
        return new DiscardPowerUseCase(gameActorManager, turnTimeoutManager);
    }
}
