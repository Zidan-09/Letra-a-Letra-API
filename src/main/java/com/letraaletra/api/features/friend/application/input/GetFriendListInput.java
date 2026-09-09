package com.letraaletra.api.features.friend.application.input;

import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetFriendListInput(
        UUID userId,
        int page,
        int size,
        Sort sort
) {
}
