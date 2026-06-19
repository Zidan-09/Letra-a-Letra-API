package com.letraaletra.api.features.friend.application.output;

import com.letraaletra.api.features.friend.domain.Friend;

public record SendFriendRequestOutput(
        Friend friend
) {
}
