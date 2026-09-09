package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendsPage;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetFriendListUseCase implements UseCase<GetFriendListInput, GetFriendListOutput> {
    private final FriendRepository friendRepository;

    public GetFriendListUseCase(
            FriendRepository friendRepository
    ) {
        this.friendRepository = friendRepository;
    }

    @Override
    public GetFriendListOutput execute(GetFriendListInput input) {
        Page<Friend> friendList = friendRepository.getFriends(
                input.userId(),
                new FriendsPage(input.page(), input.size(), input.sort())
        );

        return buildOutput(friendList);
    }

    private GetFriendListOutput buildOutput(Page<Friend> friendList) {
        return new GetFriendListOutput(
                friendList
        );
    }
}
