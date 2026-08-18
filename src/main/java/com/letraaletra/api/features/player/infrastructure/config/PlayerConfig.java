package com.letraaletra.api.features.player.infrastructure.config;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
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
            RoomTimeoutManager roomTimeoutManager,
            TurnTimeoutManager turnTimeoutManager,
            GameActorManager gameActorManager,
            GameOverService gameOverService,
            UserRepository userRepository
            ) {
        return new PlayerActionUseCase(
                roomTimeoutManager,
                turnTimeoutManager,
                gameActorManager,
                gameOverService,
                userRepository
        );
    }

    @Bean
    public DiscardPowerUseCase discardPowerUseCase(GameActorManager gameActorManager) {
        return new DiscardPowerUseCase(gameActorManager);
    }
}
