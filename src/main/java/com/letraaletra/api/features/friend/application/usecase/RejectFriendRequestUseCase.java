package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class RejectFriendRequestUseCase implements UseCase<RejectFriendRequestInput, Void> {
    private final FriendRepository friendRepository;

    public RejectFriendRequestUseCase(
            FriendRepository friendRepository
    ) {
        this.friendRepository = friendRepository;
    }

    @Override
    public Void execute(RejectFriendRequestInput input) {
        Friend request = friendRepository.find(input.userId(), input.friendId()).orElse(null);
        validateRequest(request);

        request.decline(input.userId());

        friendRepository.save(request);

        return null;
    }

    private void validateRequest(Friend request) {
        if (request == null) {
            throw new InvalidFriendRequestException();
        }
    }
}
