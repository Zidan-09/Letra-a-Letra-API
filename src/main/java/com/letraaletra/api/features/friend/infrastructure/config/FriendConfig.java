package com.letraaletra.api.features.friend.infrastructure.config;

import com.letraaletra.api.features.friend.application.input.AcceptFriendRequestInput;
import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.input.GetFriendPendingRequestsInput;
import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;
import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;
import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.application.output.GetFriendPendingRequestsOutput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.application.usecase.*;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FriendConfig {
    @Bean
    public UseCase<SendFriendRequestInput, SendFriendRequestOutput> addFriendUseCase(
            FriendRepository friendRepository,
            FriendNotifier notifier,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new SendFriendRequestUseCase(
                        friendRepository,
                        notifier
                ),
                transactions
        );
    }

    @Bean
    public UseCase<AcceptFriendRequestInput, Void> acceptFriendRequestUseCase(
            FriendRepository friendRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new AcceptFriendRequestUseCase(
                        friendRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<RejectFriendRequestInput, Void> rejectFriendRequestUseCase(
            FriendRepository friendRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RejectFriendRequestUseCase(
                        friendRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetFriendListInput, GetFriendListOutput> getFriendListUseCase(
            FriendRepository friendRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetFriendListUseCase(
                        friendRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<RemoveFriendInput, Void> removeFriendUseCase(
            FriendRepository friendRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RemoveFriendUseCase(
                        friendRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetFriendPendingRequestsInput, GetFriendPendingRequestsOutput> getFriendPendingRequestsUseCase(
            FriendRepository friendRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetFriendPendingRequestsUseCase(
                        friendRepository
                ),
                transactions
        );
    }
}
