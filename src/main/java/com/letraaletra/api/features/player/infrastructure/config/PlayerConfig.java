package com.letraaletra.api.features.player.infrastructure.config;

import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.service.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.service.GameOverHandler;
import com.letraaletra.api.features.player.application.usecase.DiscardPowerUseCase;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayerConfig {
    @Bean
    public PlayerActionUseCase playerActionUseCase(
            GameTimeoutManager gameTimeoutManager,
            TurnTimeoutManager turnTimeoutManager,
            GameActorManager gameActorManager,
            GameOverHandler gameOverHandler,
            UserRepository userRepository
            ) {
        return new PlayerActionUseCase(
                gameTimeoutManager,
                turnTimeoutManager,
                gameActorManager,
                gameOverHandler,
                userRepository
        );
    }

    @Bean
    public DiscardPowerUseCase discardPowerUseCase(GameActorManager gameActorManager) {
        return new DiscardPowerUseCase(gameActorManager);
    }
}
