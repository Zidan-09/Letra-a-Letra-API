package com.letraaletra.api.features.friend.infrastructure.config;

import com.letraaletra.api.features.friend.application.usecase.AcceptFriendRequestUseCase;
import com.letraaletra.api.features.friend.application.usecase.GetFriendListUseCase;
import com.letraaletra.api.features.friend.application.usecase.RejectFriendRequestUseCase;
import com.letraaletra.api.features.friend.application.usecase.SendFriendRequestUseCase;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FriendConfig {
    @Bean
    public SendFriendRequestUseCase addFriendUseCase(
            FriendRepository friendRepository
    ) {
        return new SendFriendRequestUseCase(
                friendRepository
        );
    }

    @Bean
    public AcceptFriendRequestUseCase acceptFriendRequestUseCase(
            FriendRepository friendRepository
    ) {
        return new AcceptFriendRequestUseCase(
                friendRepository
        );
    }

    @Bean
    public RejectFriendRequestUseCase rejectFriendRequestUseCase(
            FriendRepository friendRepository
    ) {
        return new RejectFriendRequestUseCase(
                friendRepository
        );
    }

    @Bean
    public GetFriendListUseCase getFriendListUseCase(
            FriendRepository friendRepository
    ) {
        return new GetFriendListUseCase(
                friendRepository
        );
    }
}
