package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetSentPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetSentPendingRequestsOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetSentPendingRequestsUseCase implements UseCase<GetSentPendingRequestsInput, GetSentPendingRequestsOutput> {
    private final FriendRepository friendRepository;

    public GetSentPendingRequestsUseCase(
            FriendRepository friendRepository
    ) {
        this.friendRepository = friendRepository;
    }

    @Override
    public GetSentPendingRequestsOutput execute(GetSentPendingRequestsInput input) {
        List<Friend> requests = friendRepository.getSentPendingRequests(input.userId());

        return new GetSentPendingRequestsOutput(requests);
    }
}
