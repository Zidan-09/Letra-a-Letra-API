package com.letraaletra.api.features.friend.domain.repository;

import com.letraaletra.api.features.friend.domain.Friend;

import java.util.List;
import java.util.UUID;

public interface GetFriends {
    List<Friend> getFriends(UUID userId);
    List<Friend> getPendingRequests(UUID userId);
}
