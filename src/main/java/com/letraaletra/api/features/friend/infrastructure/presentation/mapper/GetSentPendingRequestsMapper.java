package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.GetSentPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetSentPendingRequestsOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetSentPendingRequestsResponse;

import java.util.UUID;

public class GetSentPendingRequestsMapper {
    public static GetSentPendingRequestsInput toInput(UUID userId) {
        return new GetSentPendingRequestsInput(
                userId
        );
    }

    public static GetSentPendingRequestsResponse toResponse(GetSentPendingRequestsOutput output) {
        return toResponse(output, null);
    }

    public static GetSentPendingRequestsResponse toResponse(GetSentPendingRequestsOutput output, UUID viewerId) {
        return new GetSentPendingRequestsResponse(
                output.requests().stream()
                        .map(friend -> FriendResponseMapper.toResponse(friend, viewerId, output.users()))
                        .toList()
        );
    }
}
