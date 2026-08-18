package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetFriendPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetFriendPendingRequestsOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetFriendPendingRequestsUseCase implements UseCase<GetFriendPendingRequestsInput, GetFriendPendingRequestsOutput> {
    private final FriendRepository friendRepository;

    public GetFriendPendingRequestsUseCase(
            FriendRepository friendRepository
    ) {
        this.friendRepository = friendRepository;
    }

    @Override
    public GetFriendPendingRequestsOutput execute(GetFriendPendingRequestsInput input) {
        List<Friend> requests = friendRepository.getPendingRequests(input.userId());

        return buildOutput(requests);
    }

    private GetFriendPendingRequestsOutput buildOutput(List<Friend> requests) {
        return new GetFriendPendingRequestsOutput(
                requests
        );
    }
}
