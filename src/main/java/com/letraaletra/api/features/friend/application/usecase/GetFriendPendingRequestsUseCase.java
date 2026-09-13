package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetFriendPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetFriendPendingRequestsOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetFriendPendingRequestsUseCase implements UseCase<GetFriendPendingRequestsInput, GetFriendPendingRequestsOutput> {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public GetFriendPendingRequestsUseCase(
            FriendRepository friendRepository,
            UserRepository userRepository
    ) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GetFriendPendingRequestsOutput execute(GetFriendPendingRequestsInput input) {
        List<Friend> requests = friendRepository.getPendingRequests(input.userId());

        return new GetFriendPendingRequestsOutput(requests, loadUsers(input.userId(), requests));
    }

    private Map<UUID, User> loadUsers(UUID viewerId, List<Friend> requests) {
        List<UUID> ids = requests.stream()
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
