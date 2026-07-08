package com.letraaletra.api.features.levels.infrastructure.presentation.mapper;

import com.letraaletra.api.features.levels.application.input.CreateLevelInput;
import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.output.CreateLevelOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.request.CreateLevelRequest;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.request.CreateLevelRewardRequest;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.CreateLevelResponse;

import java.util.UUID;

public class CreateLevelMapper {
    public static CreateLevelInput toInput(UUID auth, CreateLevelRequest request) {
        return new CreateLevelInput(
                auth,
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
