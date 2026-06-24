package com.letraaletra.api.features.participant.infrastructure.config;

import com.letraaletra.api.features.participant.application.service.ModerationContextService;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextConfig {
    @Bean
    public ModerationContextService moderationContextService(ActorManager<Game> actorManager) {
        return new ModerationContextService(actorManager);
    }
}
