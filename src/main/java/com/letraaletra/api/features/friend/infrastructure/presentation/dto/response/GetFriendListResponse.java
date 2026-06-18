package com.letraaletra.api.features.friend.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.friend.domain.Friend;

import java.util.List;

public record GetFriendListResponse(
        List<Friend> friends
) {
}
