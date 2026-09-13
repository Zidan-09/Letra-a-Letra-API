package com.letraaletra.api.features.friend.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelFriendRequestRequest(
        @NotNull
        UUID friendId
) {
}
