package com.letraaletra.api.features.friend.infrastructure.config;

import com.letraaletra.api.features.friend.application.input.AcceptFriendRequestInput;
import com.letraaletra.api.features.friend.application.input.CancelFriendRequestInput;
import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.input.GetFriendPendingRequestsInput;
import com.letraaletra.api.features.friend.application.input.GetSentPendingRequestsInput;
import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;
import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;
import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.application.output.GetFriendPendingRequestsOutput;
import com.letraaletra.api.features.friend.application.output.GetSentPendingRequestsOutput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.application.usecase.*;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
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
            UserRepository userRepository,
            FriendNotifier notifier,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new SendFriendRequestUseCase(
                        friendRepository,
                        userRepository,
                        notifier
                ),
                transactions
        );
    }

    @Bean
    public UseCase<AcceptFriendRequestInput, Void> acceptFriendRequestUseCase(
            FriendRepository friendRepository,
            FriendNotifier notifier,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new AcceptFriendRequestUseCase(
                        friendRepository,
                        notifier
                ),
                transactions
        );
    }

    @Bean
    public UseCase<RejectFriendRequestInput, Void> rejectFriendRequestUseCase(
            FriendRepository friendRepository,
            FriendNotifier notifier,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RejectFriendRequestUseCase(
                        friendRepository,
                        notifier
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetFriendListInput, GetFriendListOutput> getFriendListUseCase(
            FriendRepository friendRepository,
            UserRepository userRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetFriendListUseCase(
                        friendRepository,
                        userRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<RemoveFriendInput, Void> removeFriendUseCase(
            FriendRepository friendRepository,
            FriendNotifier notifier,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RemoveFriendUseCase(
                        friendRepository,
                        notifier
                ),
                transactions
        );
    }

    @Bean
    public UseCase<CancelFriendRequestInput, Void> cancelFriendRequestUseCase(
            FriendRepository friendRepository,
            FriendNotifier notifier,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new CancelFriendRequestUseCase(
                        friendRepository,
                        notifier
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetFriendPendingRequestsInput, GetFriendPendingRequestsOutput> getFriendPendingRequestsUseCase(
            FriendRepository friendRepository,
            UserRepository userRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetFriendPendingRequestsUseCase(
                        friendRepository,
                        userRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetSentPendingRequestsInput, GetSentPendingRequestsOutput> getSentPendingRequestsUseCase(
            FriendRepository friendRepository,
            UserRepository userRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetSentPendingRequestsUseCase(
                        friendRepository,
                        userRepository
                ),
                transactions
        );
    }
}
