package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendResponse;

public class FriendResponseMapper {
    public static FriendResponse toResponse(Friend friend) {
        return new FriendResponse(
                friend.getUserId1(),
                friend.getUserId2(),
                friend.getStatus(),
                friend.getRequestDate()
        );
    }
}
