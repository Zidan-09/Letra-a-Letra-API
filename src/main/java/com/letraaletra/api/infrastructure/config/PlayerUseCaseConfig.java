package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.port.GameTimeOut;
import com.letraaletra.api.application.usecase.player.PlayerActionUseCase;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayerUseCaseConfig {
    @Bean
    public PlayerActionUseCase playerActionUseCase(GameRepository gameRepository, TokenService tokenService, GameTimeOut gameTimeOut) {
        return new PlayerActionUseCase(gameRepository, tokenService, gameTimeOut);
    }
}
