package com.letraaletra.api.features.levels.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateLevelRequest(
        @NotNull
        Integer level,

        @NotNull
        List<CreateLevelRewardRequest> rewards
) {
}
