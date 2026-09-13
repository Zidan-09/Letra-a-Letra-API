package com.letraaletra.api.features.friend.application.output;

import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.user.domain.User;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.UUID;

public record GetFriendListOutput(
        Page<Friend> friends,
        Map<UUID, User> users
) {
}
