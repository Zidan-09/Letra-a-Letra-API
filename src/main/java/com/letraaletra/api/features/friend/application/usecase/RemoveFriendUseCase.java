package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.exception.FriendNotFoundException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class RemoveFriendUseCase implements UseCase<RemoveFriendInput, Void> {
    private final FriendRepository friendRepository;
    private final FriendNotifier notifier;

    public RemoveFriendUseCase(
            FriendRepository friendRepository,
            FriendNotifier notifier
    ) {
        this.friendRepository = friendRepository;
        this.notifier = notifier;
    }

    @Override
    public Void execute(RemoveFriendInput input) {
        Friend friend = friendRepository.find(input.userId(), input.friendId())
                .orElseThrow(FriendNotFoundException::new);

        friend.remove(input.userId());

        friendRepository.save(friend);

        notifier.notifyFriendshipRemoved(friend.otherParty(input.userId()));

        return null;
    }
}
