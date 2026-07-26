package com.letraaletra.api.features.levels.infrastructure.presentation.mapper;

import com.letraaletra.api.features.levels.application.input.CreateLevelInput;
import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.output.CreateLevelOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.request.CreateLevelRequest;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.request.CreateLevelRewardRequest;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.CreateLevelResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public class CreateLevelMapper {
    public static CreateLevelInput toInput(AuthenticatedUser principal, CreateLevelRequest request) {
        return new CreateLevelInput(
                principal,
                request.level(),
                request.rewards().stream()
                        .map(CreateLevelMapper::toRewardInput)
                        .toList()
        );
    }

    public static CreateLevelResponse toResponse(CreateLevelOutput output) {
        return new CreateLevelResponse(
                output.level()
        );
    }

    private static CreateLevelRewardInput toRewardInput(CreateLevelRewardRequest reward) {
        return new CreateLevelRewardInput(
                reward.rewardType(),
                reward.rewardReference(),
                reward.quantity()
        );
    }
}
