package com.letraaletra.api.features.friend.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejectFriendRequestRequest(
        @NotBlank
        String friendId
) {
}
