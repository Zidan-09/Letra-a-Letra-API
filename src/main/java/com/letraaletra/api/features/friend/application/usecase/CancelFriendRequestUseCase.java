package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.CancelFriendRequestInput;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class CancelFriendRequestUseCase implements UseCase<CancelFriendRequestInput, Void> {
    private final FriendRepository friendRepository;
    private final FriendNotifier notifier;

    public CancelFriendRequestUseCase(
            FriendRepository friendRepository,
            FriendNotifier notifier
    ) {
        this.friendRepository = friendRepository;
        this.notifier = notifier;
    }

    @Override
    public Void execute(CancelFriendRequestInput input) {
        Friend request = friendRepository.find(input.userId(), input.friendId()).orElse(null);

        if (request == null) {
            throw new InvalidFriendRequestException();
        }

        request.cancel(input.userId());

        friendRepository.save(request);

        notifier.notifyFriendshipCancelled(request.otherParty(input.userId()));

        return null;
    }
}
