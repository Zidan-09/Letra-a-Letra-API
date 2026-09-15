package com.letraaletra.api.features.friend.application.output;

import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.user.application.output.EquippedItem;
import com.letraaletra.api.features.user.domain.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GetFriendPendingRequestsOutput(
        List<Friend> requests,
        Map<UUID, User> users,
        Map<UUID, List<EquippedItem>> equippedByUser
) {
}
