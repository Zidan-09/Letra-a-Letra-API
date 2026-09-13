package com.letraaletra.api.features.user.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

public record FindUserByUsernameInput(
        AuthenticatedUser principal,
        String username,
        int page,
        int size,
        Sort sort
) {
}
