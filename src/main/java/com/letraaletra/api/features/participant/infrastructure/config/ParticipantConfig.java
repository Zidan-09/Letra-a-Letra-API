package com.letraaletra.api.features.participant.infrastructure.config;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.participant.port.DisconnectScheduler;
import com.letraaletra.api.features.participant.application.usecase.*;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParticipantConfig {
    @Bean
    public BanParticipantUseCase banParticipantUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            GameActorManager gameActorManager
    ) {
        return new BanParticipantUseCase(
                userRepository,
                gameRepository,
                gameActorManager
        );
    }

    @Bean
    public DisconnectUseCase disconnectUseCase(
            GameActorManager gameActorManager,
            DisconnectScheduler disconnectScheduler,
            QueueRepository queueRepository,
            UserRepository userRepository
    ) {
        return new DisconnectUseCase(
                gameActorManager,
                disconnectScheduler,
                queueRepository,
                userRepository
        );
    }
    @Bean
    public KickParticipantUseCase kickParticipantUseCase(
            GameRepository gameRepository,
            UserRepository userRepository,
            GameActorManager gameActorManager
    ) {
        return new KickParticipantUseCase(
                gameRepository,
                userRepository,
                gameActorManager
        );
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
    public SwapRoomPositionUseCase swapRoomPositionUseCase(GameActorManager gameActorManager) {
        return new SwapRoomPositionUseCase(gameActorManager);
    }

    @Bean
    public UnbanParticipantUseCase unbanParticipantUseCase(GameActorManager gameActorManager) {
        return new UnbanParticipantUseCase(gameActorManager);
    }

    @Bean
    public RemoveDisconnectedParticipantUseCase removeDisconnectedParticipantUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            RoomTimeoutManager roomTimeoutManager,
            ActorManager<Game> actorManager,
            GameOverService gameOverService
    ) {
        return new RemoveDisconnectedParticipantUseCase(
                userRepository,
                gameRepository,
                roomTimeoutManager,
                actorManager,
                gameOverService
        );
    }
}
