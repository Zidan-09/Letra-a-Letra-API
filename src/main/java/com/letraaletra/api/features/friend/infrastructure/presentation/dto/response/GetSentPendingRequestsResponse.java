package com.letraaletra.api.features.friend.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendResponse;

import java.util.List;

public record GetSentPendingRequestsResponse(
        List<FriendResponse> requests
) {
}
