package com.letraaletra.api.features.levels.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.List;

public record CreateLevelInput(
        AuthenticatedUser principal,
        int level,
        List<CreateLevelRewardInput> rewards
) {
}
