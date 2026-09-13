package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetSentPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetSentPendingRequestsOutput;
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

public class GetSentPendingRequestsUseCase implements UseCase<GetSentPendingRequestsInput, GetSentPendingRequestsOutput> {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public GetSentPendingRequestsUseCase(
            FriendRepository friendRepository,
            UserRepository userRepository
    ) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GetSentPendingRequestsOutput execute(GetSentPendingRequestsInput input) {
        List<Friend> requests = friendRepository.getSentPendingRequests(input.userId());

        return new GetSentPendingRequestsOutput(requests, loadUsers(input.userId(), requests));
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
