package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.GetFriendPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetFriendPendingRequestsOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetFriendPendingRequestsResponse;

import java.util.UUID;

public class GetFriendPendingRequestsMapper {
    public static GetFriendPendingRequestsInput toInput(UUID userId) {
        return new GetFriendPendingRequestsInput(
                userId
        );
    }

    public static GetFriendPendingRequestsResponse toResponse(GetFriendPendingRequestsOutput output) {
        return new GetFriendPendingRequestsResponse(
                output.requests()
        );
    }
}
