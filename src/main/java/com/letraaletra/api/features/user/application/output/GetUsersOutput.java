package com.letraaletra.api.features.user.application.output;

import com.letraaletra.api.features.user.domain.User;
import org.springframework.data.domain.Page;

public record GetUsersOutput(
        Page<User> users
) {
}
