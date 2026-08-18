package com.letraaletra.api.shared.domain;

import java.util.UUID;

public record OnlineUser(
        UUID userId,
        String session
) {
}
