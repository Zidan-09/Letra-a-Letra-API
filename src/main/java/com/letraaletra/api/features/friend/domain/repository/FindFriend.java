package com.letraaletra.api.features.friend.domain.repository;

import com.letraaletra.api.features.friend.domain.Friend;

import java.util.Optional;
import java.util.UUID;

public interface FindFriend {
    Optional<Friend> find(UUID userId1, UUID userId2);
}
