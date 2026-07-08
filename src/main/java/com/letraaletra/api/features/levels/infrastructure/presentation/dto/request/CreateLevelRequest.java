package com.letraaletra.api.features.levels.infrastructure.presentation.dto.request;

import java.util.List;

public record CreateLevelRequest(
        Integer level,
        List<CreateLevelRewardRequest> rewards
) {
}
