package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.BanUserInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.BanUserRequest;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class BanUserMapper {
    public static BanUserInput toInput(AuthenticatedUser principal, UUID userId, BanUserRequest request) {
        return new BanUserInput(
                principal,
                userId,
                request.type(),
                request.expiresIn(),
                request.reason()
        );
    }
}
