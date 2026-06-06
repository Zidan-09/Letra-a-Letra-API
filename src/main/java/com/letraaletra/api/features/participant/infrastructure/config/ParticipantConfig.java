package com.letraaletra.api.features.participant.infrastructure.config;

import com.letraaletra.api.features.participant.application.service.ModerationContextService;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.DisconnectScheduler;
import com.letraaletra.api.features.participant.application.usecase.*;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParticipantConfig {
    @Bean
    public BanParticipantUseCase banParticipantUseCase(
            ModerationContextService moderationContextService,
            UserRepository userRepository,
            GameActorManager gameActorManager
    ) {
        return new BanParticipantUseCase(
                moderationContextService,
                userRepository,
                gameActorManager
        );
    }

    @Bean
    public DisconnectUseCase disconnectUseCase(
            GameActorManager gameActorManager,
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
            ModerationContextService moderationContextService,
            GameActorManager gameActorManager
    ) {
        return new KickParticipantUseCase(moderationContextService, gameActorManager);
    }

    @Bean
    public ReconnectUseCase reconnectUseCase(
            ActorManager<Game> actorManager,
            DisconnectScheduler disconnectScheduler,
            UserRepository userRepository
            ) {
        return new ReconnectUseCase(
                actorManager,
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
    public RemoveParticipantUseCase removeParticipantUseCase(
            GameActorManager gameActorManager,
            GameRepository gameRepository,
            UserRepository userRepository
    ) {
        return new RemoveParticipantUseCase(gameActorManager, gameRepository, userRepository);
    }
}
