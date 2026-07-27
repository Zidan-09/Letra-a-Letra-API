package com.letraaletra.api.features.levels.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateLevelRequest(
        @NotNull
        Integer level,

        @NotNull
        List<UpdateLevelRewardRequest> rewards
) {
}
