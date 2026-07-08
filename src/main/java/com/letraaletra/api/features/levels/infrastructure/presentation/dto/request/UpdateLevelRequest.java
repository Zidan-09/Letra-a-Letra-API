package com.letraaletra.api.features.levels.infrastructure.presentation.dto.request;

import java.util.List;

public record UpdateLevelRequest(
        Integer level,
        List<UpdateLevelRewardRequest> rewards
) {
}
