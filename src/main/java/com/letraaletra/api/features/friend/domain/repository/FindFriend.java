package com.letraaletra.api.features.friend.domain.repository;

import com.letraaletra.api.features.friend.domain.Friend;

import java.util.Optional;

public interface FindFriend {
    Optional<Friend> find(String userId1, String userId2);
}
