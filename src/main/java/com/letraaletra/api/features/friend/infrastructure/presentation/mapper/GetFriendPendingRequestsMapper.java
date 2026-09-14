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
        return toResponse(output, null, null);
    }

    public static GetFriendPendingRequestsResponse toResponse(GetFriendPendingRequestsOutput output, UUID viewerId) {
        return toResponse(output, viewerId, null);
    }

    public static GetFriendPendingRequestsResponse toResponse(GetFriendPendingRequestsOutput output, UUID viewerId, java.util.Map<UUID, java.util.List<com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse>> equippedByUser) {
        return new GetFriendPendingRequestsResponse(
                output.requests().stream()
                        .map(friend -> FriendResponseMapper.toResponse(friend, viewerId, output.users(), equippedByUser))
                        .toList()
        );
    }
}
