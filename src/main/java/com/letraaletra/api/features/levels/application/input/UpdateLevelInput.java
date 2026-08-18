package com.letraaletra.api.features.levels.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.List;
import java.util.UUID;

public record UpdateLevelInput(
        AuthenticatedUser principal,
        UUID levelId,
        int level,
        List<CreateLevelRewardInput> rewards
) {
}
