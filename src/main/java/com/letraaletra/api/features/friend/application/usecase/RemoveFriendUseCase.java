package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;
import com.letraaletra.api.features.friend.domain.Friend;
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

        friend.remove();

        friendRepository.save(friend);

        return null;
    }
}
