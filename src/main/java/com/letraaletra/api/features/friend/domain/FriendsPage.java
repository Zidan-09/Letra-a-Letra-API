package com.letraaletra.api.features.friend.domain;

import org.springframework.data.domain.Sort;

public record FriendsPage(
        int page,
        int size,
        Sort sort
) {
}
