package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.port.DisconnectScheduler;
import com.letraaletra.api.application.service.LeaveGameService;
import com.letraaletra.api.application.usecase.participant.*;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.MatchmakingRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.infrastructure.manager.GameActorManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParticipantUseCaseConfig {
    @Bean
    public BanParticipantUseCase banParticipantUseCase(
            ModerationContextFactory moderationContextFactory,
            UserRepository userRepository,
            GameRepository gameRepository,
            GameActorManager gameActorManager
    ) {
        return new BanParticipantUseCase(
                moderationContextFactory,
                userRepository,
                gameRepository,
                gameActorManager
        );
    }

    @Bean
    public DisconnectUseCase disconnectUseCase(
            GameRepository gameRepository,
            DisconnectScheduler disconnectScheduler,
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository
    ) {
        return new DisconnectUseCase(
                gameRepository,
                disconnectScheduler,
                matchmakingRepository,
                userRepository
        );
    }
    @Bean
    public KickParticipantUseCase kickParticipantUseCase(
            ModerationContextFactory moderationContextFactory,
            GameActorManager gameActorManager
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
    public SwapRoomPositionUseCase swapRoomPositionUseCase(GameActorManager gameActorManager, TokenService tokenService) {
        return new SwapRoomPositionUseCase(gameActorManager, tokenService);
    }

    @Bean
    public UnbanUserUseCase unbanUserUseCase(TokenService tokenService, GameActorManager gameActorManager) {
        return new UnbanUserUseCase(tokenService, gameActorManager);
    }

    @Bean
    public LeaveGameService leaveGameService(GameRepository gameRepository, UserRepository userRepository) {
        return new LeaveGameService(gameRepository, userRepository);
    }
}
