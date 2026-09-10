package com.letraaletra.api.features.friend.application.output;

import com.letraaletra.api.features.friend.domain.Friend;
import org.springframework.data.domain.Page;

public record GetFriendListOutput(
        Page<Friend> friends
) {
}
