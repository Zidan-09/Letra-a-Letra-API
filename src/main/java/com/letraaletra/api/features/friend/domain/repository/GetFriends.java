package com.letraaletra.api.features.friend.domain.repository;

import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendsPage;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface GetFriends {
    Page<Friend> getFriends(UUID userId, FriendsPage page);
    List<Friend> getPendingRequests(UUID userId);
}
