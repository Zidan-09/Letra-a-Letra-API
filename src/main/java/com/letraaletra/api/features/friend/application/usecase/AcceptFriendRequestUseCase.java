package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.AcceptFriendRequestInput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.usecase.UseCaseWithoutOutput;

public class AcceptFriendRequestUseCase implements UseCaseWithoutOutput<AcceptFriendRequestInput> {
    private final FriendRepository friendRepository;

    public AcceptFriendRequestUseCase(
            FriendRepository friendRepository
    ) {
        this.friendRepository = friendRepository;
    }

    @Override
    public void execute(AcceptFriendRequestInput input) {
        Friend request = friendRepository.find(input.userId(), input.friendId());
        validateRequest(request);

        request.setStatus(FriendStatus.ACCEPT);

        friendRepository.save(request);
    }

    private void validateRequest(Friend request) {
        if (request == null || !request.getStatus().equals(FriendStatus.PENDING)) {
            throw new InvalidFriendRequestException();
        }
    }
}
