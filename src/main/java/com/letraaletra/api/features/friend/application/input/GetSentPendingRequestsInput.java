package com.letraaletra.api.features.friend.application.input;

import java.util.UUID;

public record GetSentPendingRequestsInput(
        UUID userId
) {
}
