package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendsPage;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetFriendListUseCase implements UseCase<GetFriendListInput, GetFriendListOutput> {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public GetFriendListUseCase(
            FriendRepository friendRepository,
            UserRepository userRepository
    ) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GetFriendListOutput execute(GetFriendListInput input) {
        Page<Friend> friendList = friendRepository.getFriends(
                input.userId(),
                new FriendsPage(input.page(), input.size(), input.sort())
        );

        return new GetFriendListOutput(friendList, loadUsers(input.userId(), friendList.getContent()));
    }

    private Map<UUID, User> loadUsers(UUID viewerId, List<Friend> friends) {
        List<UUID> ids = friends.stream()
                .filter(friend -> friend.isParticipant(viewerId))
                .map(friend -> friend.otherParty(viewerId))
                .distinct()
                .toList();

        if (ids.isEmpty()) {
            return Map.of();
        }

        return userRepository.findUsersById(ids).stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity(), (first, second) -> first));
    }
}
