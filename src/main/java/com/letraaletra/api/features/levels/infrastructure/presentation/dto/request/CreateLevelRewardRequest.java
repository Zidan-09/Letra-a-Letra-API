package com.letraaletra.api.features.levels.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.offers.domain.RewardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateLevelRewardRequest(
        @NotNull
        @NotBlank
        RewardType rewardType,

        @NotNull
        @NotBlank
        UUID rewardReference,

        @NotNull
        int quantity
) {
}
