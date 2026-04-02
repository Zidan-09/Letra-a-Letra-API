package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.context.PlayerGameContextFactory;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextConfig {
    @Bean
    public ModerationContextFactory moderationContextFactory(GameRepository gameRepository, TokenService tokenService) {
        return new ModerationContextFactory(gameRepository, tokenService);
    }

    @Bean
    public PlayerGameContextFactory playerGameContextFactory(GameRepository gameRepository, UserRepository userRepository) {
        return new PlayerGameContextFactory(gameRepository, userRepository);
    }
}
