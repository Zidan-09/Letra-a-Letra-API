package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.GrantUserRewardInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.GrantUserRewardRequest;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class GrantUserRewardMapper {
    public static GrantUserRewardInput toInput(AuthenticatedUser principal, UUID userId, GrantUserRewardRequest request) {
        return new GrantUserRewardInput(
                principal,
                userId,
                request.rewardType(),
                request.rewardReference(),
                request.quantity()
        );
    }
}
