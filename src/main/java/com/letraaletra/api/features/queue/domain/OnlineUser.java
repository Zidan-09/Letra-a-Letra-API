package com.letraaletra.api.features.queue.domain;

import java.util.UUID;

public record OnlineUser(
        UUID userId,
        String session
) {
}
