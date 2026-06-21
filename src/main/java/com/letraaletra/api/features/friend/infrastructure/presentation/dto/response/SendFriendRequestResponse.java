package com.letraaletra.api.features.friend.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.friend.domain.Friend;

public record SendFriendRequestResponse(
    Friend request
) {
}
