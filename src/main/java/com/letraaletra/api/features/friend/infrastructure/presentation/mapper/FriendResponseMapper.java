package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendDirection;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendProfileResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendResponse;
import com.letraaletra.api.features.user.application.output.EquippedItem;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.InventoryItemResponseMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FriendResponseMapper {
    public static FriendResponse toResponse(Friend friend) {
        return toResponse(friend, null, Map.of(), Map.of());
    }

    public static FriendResponse toResponse(Friend friend, UUID viewerId) {
        return toResponse(friend, viewerId, Map.of(), Map.of());
    }

    public static FriendResponse toResponse(Friend friend, UUID viewerId, Map<UUID, User> users) {
        return toResponse(friend, viewerId, users, Map.of());
    }

    public static FriendResponse toResponse(Friend friend, UUID viewerId, Map<UUID, User> users, Map<UUID, List<EquippedItem>> equippedByUser) {
        UUID friendId = friend.getUserId2();
        FriendDirection direction = null;
        FriendProfileResponse profile = null;

        if (viewerId != null && friend.isParticipant(viewerId)) {
            friendId = friend.otherParty(viewerId);
            direction = friend.sentBy(viewerId) ? FriendDirection.SENT : FriendDirection.RECEIVED;

            User user = users != null ? users.get(friendId) : null;
            if (user != null) {
                List<EquippedItem> equipped = equippedByUser == null ? List.of() : equippedByUser.getOrDefault(friendId, List.of());
                profile = FriendProfileMapper.toResponse(user, InventoryItemResponseMapper.toResponses(equipped));
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
