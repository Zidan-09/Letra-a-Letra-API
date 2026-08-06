package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.UnbanUserInput;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class UnbanUserMapper {
    public static UnbanUserInput toInput(AuthenticatedUser principal, UUID userId) {
        return new UnbanUserInput(
                principal,
                userId
        );
    }
}
