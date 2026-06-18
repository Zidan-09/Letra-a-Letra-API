package com.letraaletra.api.features.friend.application.output;

import com.letraaletra.api.features.friend.domain.Friend;

import java.util.List;

public record GetFriendListOutput(
        List<Friend> friends
) {
}
