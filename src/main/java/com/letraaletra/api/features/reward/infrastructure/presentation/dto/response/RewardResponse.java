package com.letraaletra.api.features.reward.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticResponse;
import com.letraaletra.api.features.reward.domain.RewardType;

public record RewardResponse(
        RewardType type,
        Integer amount,
        CosmeticResponse cosmetic
) {
}
