package com.letraaletra.api.shared.infrastructure.presentation.dto.response.reward;

import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticResponse;
import com.letraaletra.api.features.offers.domain.RewardType;

public record RewardResponse(
        RewardType type,
        Integer amount,
        CosmeticResponse cosmetic
) {
}
