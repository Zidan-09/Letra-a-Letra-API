package com.letraaletra.api.features.friend.domain.repository;

import com.letraaletra.api.features.friend.domain.Friend;

public interface FindFriend {
    Friend find(String userId1, String userId2);
}
