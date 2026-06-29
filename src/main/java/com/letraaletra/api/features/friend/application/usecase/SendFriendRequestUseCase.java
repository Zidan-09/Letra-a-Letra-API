package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.UUID;

public class SendFriendRequestUseCase implements UseCase<SendFriendRequestInput, SendFriendRequestOutput> {
    private final FriendRepository friendRepository;
    private final FriendNotifier notifier;

    public SendFriendRequestUseCase(
            FriendRepository friendRepository,
            FriendNotifier notifier
    ) {
        this.friendRepository = friendRepository;
        this.notifier = notifier;
    }

    @Override
    public SendFriendRequestOutput execute(SendFriendRequestInput input) {
        checkFriendRequest(input.userId(), input.friendId());

        Friend friend = Friend.create(
                input.userId(),
                input.friendId()
        );

        friendRepository.save(friend);

        notifier.notifierUser(input.friendId(), input.senderNickname());

        return buildOutput(friend);
    }

    private void checkFriendRequest(UUID userId, UUID friendId) {
        Friend request = friendRepository.find(userId, friendId).orElse(null);

        if (request != null && !request.getStatus().equals(FriendStatus.DECLINED)) {
            throw new InvalidFriendRequestException();
        }
    }

    private SendFriendRequestOutput buildOutput(Friend friend) {
        return new SendFriendRequestOutput(
                friend
        );
    }
}
