package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class RejectFriendRequestUseCase implements UseCase<RejectFriendRequestInput, Void> {
    private final FriendRepository friendRepository;
    private final FriendNotifier notifier;

    public RejectFriendRequestUseCase(
            FriendRepository friendRepository,
            FriendNotifier notifier
    ) {
        this.friendRepository = friendRepository;
        this.notifier = notifier;
    }

    @Override
    public Void execute(RejectFriendRequestInput input) {
        Friend request = friendRepository.find(input.userId(), input.friendId()).orElse(null);
        validateRequest(request);

        request.decline(input.userId());

        friendRepository.save(request);

        notifier.notifyFriendshipDeclined(request.otherParty(input.userId()));

        return null;
    }

    private void validateRequest(Friend request) {
        if (request == null) {
            throw new InvalidFriendRequestException();
        }
    }
}
