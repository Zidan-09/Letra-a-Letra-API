package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.context.PlayerGameContextFactory;
import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.usecase.participant.*;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParticipantUseCaseConfig {
    @Bean
    public BanParticipantUseCase banParticipantUseCase(GameRepository gameRepository, ModerationContextFactory moderationContextFactory) {
        return new BanParticipantUseCase(gameRepository, moderationContextFactory);
    }

    @Bean
    public DisconnectUseCase disconnectUseCase(GameRepository gameRepository, PlayerGameContextFactory playerGameContextFactory, DisconnectScheduler disconnectScheduler) {
        return new DisconnectUseCase(gameRepository, playerGameContextFactory, disconnectScheduler);
    }
    @Bean
    public KickParticipantUseCase kickParticipantUseCase(GameRepository gameRepository, ModerationContextFactory moderationContextFactory) {
        return new KickParticipantUseCase(gameRepository, moderationContextFactory);
    }

    @Bean
    public ReconnectUseCase reconnectUseCase(GameRepository gameRepository, PlayerGameContextFactory playerGameContextFactory, DisconnectScheduler disconnectScheduler) {
        return new ReconnectUseCase(gameRepository, playerGameContextFactory, disconnectScheduler);
    }

    @Bean
    public SwapRoomPositionUseCase swapRoomPositionUseCase(GameRepository gameRepository, TokenService tokenService) {
        return new SwapRoomPositionUseCase(gameRepository, tokenService);
    }

    @Bean
    public UnbanUserUseCase unbanUserUseCase(GameRepository gameRepository, TokenService tokenService) {
        return new UnbanUserUseCase(gameRepository, tokenService);
    }
}
