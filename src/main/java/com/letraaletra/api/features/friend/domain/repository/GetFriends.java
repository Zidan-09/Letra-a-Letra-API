package com.letraaletra.api.features.friend.domain.repository;

import com.letraaletra.api.features.friend.domain.Friend;

import java.util.List;

public interface GetFriends {
    List<Friend> getFriends(String userId);
}
