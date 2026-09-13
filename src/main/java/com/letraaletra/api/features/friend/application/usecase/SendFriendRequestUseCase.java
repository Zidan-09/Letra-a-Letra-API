package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.FriendNotFoundException;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.Map;
import java.util.UUID;

public class SendFriendRequestUseCase implements UseCase<SendFriendRequestInput, SendFriendRequestOutput> {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendNotifier notifier;

    public SendFriendRequestUseCase(
            FriendRepository friendRepository,
            UserRepository userRepository,
            FriendNotifier notifier
    ) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.notifier = notifier;
    }

    @Override
    public SendFriendRequestOutput execute(SendFriendRequestInput input) {
        if (input.userId() == null || input.friendId() == null) {
            throw new InvalidFriendRequestException();
        }

        if (input.userId().equals(input.friendId())) {
            throw new InvalidFriendRequestException();
        }

        if (!userRepository.exists(input.friendId())) {
            throw new FriendNotFoundException();
        }

        Friend existing = friendRepository.find(input.userId(), input.friendId()).orElse(null);

        if (existing == null) {
            Friend friend = Friend.create(
                    input.userId(),
                    input.friendId()
            );

            friendRepository.save(friend);

            notifier.notifierUser(input.friendId());

            return buildOutput(friend, loadTarget(input.friendId()));
        }

        if (!existing.getStatus().equals(FriendStatus.DECLINED)) {
            throw new InvalidFriendRequestException();
        }

        existing.reopen();

        friendRepository.save(existing);

        notifier.notifierUser(existing.otherParty(input.userId()));

        return buildOutput(existing, loadTarget(input.friendId()));
    }

    private Map<UUID, User> loadTarget(UUID targetId) {
        return userRepository.find(targetId)
                .map(user -> Map.of(user.getUserId(), user))
                .orElseGet(Map::of);
    }

    private SendFriendRequestOutput buildOutput(Friend friend, Map<UUID, User> users) {
        return new SendFriendRequestOutput(
                friend,
                users
        );
    }
}
