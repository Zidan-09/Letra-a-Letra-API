package com.letraaletra.api.features.user.application.output;

import com.letraaletra.api.features.user.domain.User;

public record GetMyProfileOutput(
        User user
) {
}
