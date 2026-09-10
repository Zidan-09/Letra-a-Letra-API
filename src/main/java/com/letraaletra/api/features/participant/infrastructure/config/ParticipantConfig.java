package com.letraaletra.api.features.participant.infrastructure.config;

import com.letraaletra.api.features.game.application.port.GameOverFinalizer;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.participant.port.DisconnectScheduler;
import com.letraaletra.api.features.participant.application.input.BanParticipantInput;
import com.letraaletra.api.features.participant.application.input.DisconnectParticipantInput;
import com.letraaletra.api.features.participant.application.input.KickParticipantInput;
import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.input.RemoveDisconnectedParticipantInput;
import com.letraaletra.api.features.participant.application.input.SwapPositionInput;
import com.letraaletra.api.features.participant.application.input.UnbanParticipantInput;
import com.letraaletra.api.features.participant.application.output.BanParticipantOutput;
import com.letraaletra.api.features.participant.application.output.DisconnectParticipantOutput;
import com.letraaletra.api.features.participant.application.output.KickParticipantOutput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.participant.application.output.SwapPositionOutput;
import com.letraaletra.api.features.participant.application.output.UnbanParticipantOutput;
import com.letraaletra.api.features.participant.application.usecase.*;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class ParticipantConfig {
    @Bean
    public UseCase<BanParticipantInput, BanParticipantOutput> banParticipantUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            GameActorManager gameActorManager,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new BanParticipantUseCase(
                        userRepository,
                        gameRepository,
                        gameActorManager
                ),
                transactions
        );
    }

    @Bean
    public UseCase<DisconnectParticipantInput, Optional<DisconnectParticipantOutput>> disconnectUseCase(
            GameActorManager gameActorManager,
            DisconnectScheduler disconnectScheduler,
            QueueRepository queueRepository,
            UserRepository userRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new DisconnectUseCase(
                        gameActorManager,
                        disconnectScheduler,
                        queueRepository,
                        userRepository
                ),
                transactions
        );
    }
    @Bean
    public UseCase<KickParticipantInput, KickParticipantOutput> kickParticipantUseCase(
            GameRepository gameRepository,
            UserRepository userRepository,
            GameActorManager gameActorManager,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new KickParticipantUseCase(
                        gameRepository,
                        userRepository,
                        gameActorManager
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ReconnectParticipantInput, Optional<ReconnectParticipantOutput>> reconnectUseCase(
            ActorManager<Game> actorManager,
            DisconnectScheduler disconnectScheduler,
            UserRepository userRepository,
            TransactionalExecutorService transactions
            ) {
        return new TransactionalUseCase<>(
                new ReconnectUseCase(
                        actorManager,
                        disconnectScheduler,
                        userRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<SwapPositionInput, SwapPositionOutput> swapRoomPositionUseCase(
            GameActorManager gameActorManager,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new SwapRoomPositionUseCase(gameActorManager),
                transactions
        );
    }

    @Bean
    public UseCase<UnbanParticipantInput, UnbanParticipantOutput> unbanParticipantUseCase(
            GameActorManager gameActorManager,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new UnbanParticipantUseCase(gameActorManager),
                transactions
        );
    }

    @Bean
    public UseCase<RemoveDisconnectedParticipantInput, Void> removeDisconnectedParticipantUseCase(
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            GameOverFinalizer gameOverFinalizer,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RemoveDisconnectedParticipantUseCase(
                        userRepository,
                        actorManager,
                        gameOverFinalizer
                ),
                transactions
        );
    }
}
