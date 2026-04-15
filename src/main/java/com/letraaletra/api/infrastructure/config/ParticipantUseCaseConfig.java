package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.port.DisconnectScheduler;
import com.letraaletra.api.application.usecase.participant.*;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.MatchmakingRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.infrastructure.manager.GameActorRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParticipantUseCaseConfig {
    @Bean
    public BanParticipantUseCase banParticipantUseCase(
            ModerationContextFactory moderationContextFactory,
            UserRepository userRepository,
            GameActorRepository gameActorManager
    ) {
        return new BanParticipantUseCase(
                moderationContextFactory,
                userRepository,
                gameActorManager
        );
    }

    @Bean
    public DisconnectUseCase disconnectUseCase(
            GameActorRepository gameActorManager,
            DisconnectScheduler disconnectScheduler,
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository
    ) {
        return new DisconnectUseCase(
                gameActorManager,
                disconnectScheduler,
                matchmakingRepository,
                userRepository
        );
    }
    @Bean
    public KickParticipantUseCase kickParticipantUseCase(
            ModerationContextFactory moderationContextFactory,
            GameActorRepository gameActorManager
    ) {
        return new KickParticipantUseCase(moderationContextFactory, gameActorManager);
    }

    @Bean
    public ReconnectUseCase reconnectUseCase(
            GameRepository gameRepository,
            DisconnectScheduler disconnectScheduler,
            UserRepository userRepository
            ) {
        return new ReconnectUseCase(
                gameRepository,
                disconnectScheduler,
                userRepository
        );
    }

    @Bean
    public SwapRoomPositionUseCase swapRoomPositionUseCase(GameActorRepository gameActorManager, TokenService tokenService) {
        return new SwapRoomPositionUseCase(gameActorManager, tokenService);
    }

    @Bean
    public UnbanUserUseCase unbanUserUseCase(TokenService tokenService, GameActorRepository gameActorManager) {
        return new UnbanUserUseCase(tokenService, gameActorManager);
    }

    @Bean
    public RemoveParticipantUseCase removeParticipantUseCase(
            GameActorRepository gameActorManager,
            GameRepository gameRepository,
            UserRepository userRepository
    ) {
        return new RemoveParticipantUseCase(gameActorManager, gameRepository, userRepository);
    }
}
