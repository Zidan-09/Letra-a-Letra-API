package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.FriendNotFoundException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class RemoveFriendUseCase implements UseCase<RemoveFriendInput, Void> {
    private final FriendRepository friendRepository;

    public RemoveFriendUseCase(
            FriendRepository friendRepository
    ) {
        this.friendRepository = friendRepository;
    }

    @Override
    public Void execute(RemoveFriendInput input) {
        Friend friend = friendRepository.find(input.userId(), input.friendId())
                .orElseThrow(FriendNotFoundException::new);

        validateFriend(friend);

        friend.setStatus(FriendStatus.DECLINED);

        friendRepository.save(friend);

        return null;
    }

    private void validateFriend(Friend friend) {
        if (!friend.getStatus().equals(FriendStatus.ACCEPT)) {
            throw new FriendNotFoundException();
        }
    }
}
