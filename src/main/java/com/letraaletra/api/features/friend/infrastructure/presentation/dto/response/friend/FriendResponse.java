package com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend;

import com.letraaletra.api.features.friend.domain.FriendStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record FriendResponse(
        UUID userId1,
        UUID userId2,
        FriendStatus status,
        LocalDateTime requestDate
) {
}
