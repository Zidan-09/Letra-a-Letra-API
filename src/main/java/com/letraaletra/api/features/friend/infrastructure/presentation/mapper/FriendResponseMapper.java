package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendDirection;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendProfileResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendResponse;
import com.letraaletra.api.features.user.domain.User;

import java.util.Map;
import java.util.UUID;

public class FriendResponseMapper {
    public static FriendResponse toResponse(Friend friend) {
        return toResponse(friend, null, Map.of());
    }

    public static FriendResponse toResponse(Friend friend, UUID viewerId) {
        return toResponse(friend, viewerId, Map.of());
    }

    public static FriendResponse toResponse(Friend friend, UUID viewerId, Map<UUID, User> users) {
        UUID friendId = friend.getUserId2();
        FriendDirection direction = null;
        FriendProfileResponse profile = null;

        if (viewerId != null && friend.isParticipant(viewerId)) {
            friendId = friend.otherParty(viewerId);
            direction = friend.sentBy(viewerId) ? FriendDirection.SENT : FriendDirection.RECEIVED;

            User user = users != null ? users.get(friendId) : null;
            if (user != null) {
                profile = FriendProfileMapper.toResponse(user);
            }
        }

        return new FriendResponse(
                friend.getUserId1(),
                friend.getUserId2(),
                friendId,
                direction,
                friend.getStatus(),
                friend.getRequestDate(),
                profile
        );
    }
}
