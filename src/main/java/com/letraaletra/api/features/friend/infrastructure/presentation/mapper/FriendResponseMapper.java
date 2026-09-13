package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendDirection;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendResponse;

import java.util.UUID;

public class FriendResponseMapper {
    public static FriendResponse toResponse(Friend friend) {
        return toResponse(friend, null);
    }

    public static FriendResponse toResponse(Friend friend, UUID viewerId) {
        UUID friendId = friend.getUserId2();
        FriendDirection direction = null;

        if (viewerId != null && friend.isParticipant(viewerId)) {
            friendId = friend.otherParty(viewerId);
            direction = friend.sentBy(viewerId) ? FriendDirection.SENT : FriendDirection.RECEIVED;
        }

        return new FriendResponse(
                friend.getUserId1(),
                friend.getUserId2(),
                friendId,
                direction,
                friend.getStatus(),
                friend.getRequestDate()
        );
    }
}
