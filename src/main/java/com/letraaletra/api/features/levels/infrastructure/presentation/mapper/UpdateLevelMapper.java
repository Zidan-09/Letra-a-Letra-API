package com.letraaletra.api.features.levels.infrastructure.presentation.mapper;

import com.letraaletra.api.features.levels.application.input.CreateLevelRewardInput;
import com.letraaletra.api.features.levels.application.input.UpdateLevelInput;
import com.letraaletra.api.features.levels.application.output.UpdateLevelOutput;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.request.UpdateLevelRequest;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.request.UpdateLevelRewardRequest;
import com.letraaletra.api.features.levels.infrastructure.presentation.dto.response.UpdateLevelResponse;

import java.util.UUID;

public class UpdateLevelMapper {
    public static UpdateLevelInput toInput(UUID auth, UUID levelId, UpdateLevelRequest request) {
        return new UpdateLevelInput(
                auth,
                levelId,
                request.level(),
                request.rewards().stream()
                        .map(UpdateLevelMapper::toRewardInput)
                        .toList()
        );
    }

    public static UpdateLevelResponse toResponse(UpdateLevelOutput output) {
        return new UpdateLevelResponse(
                output.level()
        );
    }

    private static CreateLevelRewardInput toRewardInput(UpdateLevelRewardRequest reward) {
        return new CreateLevelRewardInput(
                reward.rewardType(),
                reward.rewardReference(),
                reward.quantity()
        );
    }
}
