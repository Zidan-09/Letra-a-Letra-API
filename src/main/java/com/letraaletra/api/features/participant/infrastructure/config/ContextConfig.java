package com.letraaletra.api.features.participant.infrastructure.config;

import com.letraaletra.api.features.participant.application.service.ModerationContextService;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextConfig {
    @Bean
    public ModerationContextService moderationContextService(ActorManager<Game> actorManager, TokenService tokenService) {
        return new ModerationContextService(actorManager, tokenService);
    }
}
